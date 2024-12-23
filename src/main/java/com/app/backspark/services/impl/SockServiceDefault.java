package com.app.backspark.services.impl;

import com.app.backspark.mappers.SockMapper;
import com.app.backspark.models.Sock;
import com.app.backspark.repositories.SockRepository;
import com.app.backspark.services.SockService;
import com.app.backspark.views.SockView;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SockServiceDefault implements SockService {
    private static final Logger logger = LoggerFactory.getLogger(SockServiceDefault.class);
    private final SockRepository sockRepository;
    private final SockMapper sockMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Sock> registerIncome(Set<SockView> socksToSave) {
        Set<Sock> socksDB = socksToSave.stream()
                .map(this::processSock)
                .collect(Collectors.toSet());

        List<Sock> socks = sockRepository.saveAll(socksDB);
        socks.forEach(sock -> logger.info("Registered income: color={}, cottonPercentage={}, quantity={}, actual quantity={}",
                sock.getColor(),
                sock.getCottonPercentage(),
                sock.getQuantity(),
                sock.getQuantity()));
        return socks;
    }

    private Sock processSock(SockView sockView) {
        Sock sockDB = sockRepository.findByColorAndCottonPercentage(sockView.getColor(), sockView.getCottonPercentage())
                .orElse(sockMapper.toEntity(new Sock(), sockView));
        sockDB.setQuantity(sockDB.getQuantity() + sockView.getQuantity());
        return sockDB;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Sock registerOutcome(SockView sock) {
        Sock sockDB = sockRepository.findByColorAndCottonPercentage(sock.getColor(), sock.getCottonPercentage())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("sock with color %s and cotton percentage %d not found", sock.getColor(), sock.getCottonPercentage())));

        if (sockDB.getQuantity() < sock.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("not enough socks in stock, socks in stock: %d, but outcome quantity is %d", sockDB.getQuantity(), sock.getQuantity()));
        }

        sockDB.setQuantity(sockDB.getQuantity() - sock.getQuantity());
        logger.info("Registered income: color={}, cottonPercentage={}, quantity={}, actual quantity={}", sock.getColor(), sock.getCottonPercentage(), sock.getQuantity(), sockDB.getQuantity());
        return sockDB;
    }

    @Override
    @Transactional(readOnly = true)
    public long getFilteredSocksCount(String color, String cottonPercentageOperator, Double cottonPercentage, Double minCottonPercentage, Double maxCottonPercentage) {
        if (cottonPercentageOperator == null) {
            return sockRepository.sumQuantityByColor(color);
        }

        return switch (cottonPercentageOperator) {
            case "moreThan" -> sockRepository.sumQuantityByColorAndCottonPercentageMoreThan(color, cottonPercentage);
            case "lessThan" -> sockRepository.sumQuantityByColorAndCottonPercentageLessThan(color, cottonPercentage);
            case "equal" -> sockRepository.sumQuantityByColorAndCottonPercentageEquals(color, cottonPercentage);
            case "between" ->
                    sockRepository.findQuantityByColorAndCottonRange(color, minCottonPercentage, maxCottonPercentage);
            default ->
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid operator for cotton percentage");
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Sock updateSock(Long id, SockView sock) {
        if (!id.equals(sock.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ids are not the same");
        }
        if (sockRepository.findByColorAndCottonPercentageAndIdNot(sock.getColor(), sock.getCottonPercentage(), id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Sock with color %s and cotton percentage %d already exists", sock.getColor(), sock.getCottonPercentage()));
        }
        Sock sockDB = sockRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Sock with id %d not found", id)));
        sockDB = sockMapper.toEntity(sockDB, sock);
        logger.info("Updated sock: id={}, color={}, cottonPercentage={}, quantity={}", id, sock.getColor(), sock.getCottonPercentage(), sock.getQuantity());
        return sockDB;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SockView> uploadSocksFromCsv(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        Set<SockView> socksToSave = new HashSet<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            reader.readNext(); // first line
            while ((nextLine = reader.readNext()) != null) {
                SockView sockView = parseLineToSock(nextLine);
                socksToSave.add(sockView);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading CSV file");
        } catch (CsvValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file is invalid");
        }
        if (socksToSave.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid socks data in file");
        }

        return registerIncome(socksToSave).stream()
                .map(sockMapper::toView)
                .toList();
    }

    private SockView parseLineToSock(String[] fields) {
        if (fields.length != 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CSV format: each row must have 3 columns");
        }
        try {
            return new SockView(
                    fields[0].trim(), // color
                    Double.parseDouble(fields[1].trim()), // cottonPercentage
                    Integer.parseInt(fields[2].trim()) // quantity
            );
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid number format in CSV file", e);
        }
    }
}

