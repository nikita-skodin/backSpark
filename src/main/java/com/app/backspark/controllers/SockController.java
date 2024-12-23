package com.app.backspark.controllers;

import com.app.backspark.mappers.SockMapper;
import com.app.backspark.models.Sock;
import com.app.backspark.services.SockService;
import com.app.backspark.views.SockView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/socks")
@RequiredArgsConstructor
public class SockController {
    private final SockService sockService;
    private final SockMapper sockMapper;

    @PostMapping("/income")
    public ResponseEntity<SockView> registerIncome(@Valid @RequestBody SockView sockView) {
        Sock result = sockService.registerIncome(Set.of(sockView)).get(0);
        return ResponseEntity.ok().body(sockMapper.toView(result));
    }

    @PostMapping("/outcome")
    public ResponseEntity<SockView> registerOutcome(@Valid @RequestBody SockView sockView) {
        Sock result = sockService.registerOutcome(sockView);
        return ResponseEntity.ok().body(sockMapper.toView(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SockView> updateSock(@PathVariable Long id, @RequestBody SockView sockView) {
        Sock result = sockService.updateSock(id, sockView);
        return ResponseEntity.ok().body(sockMapper.toView(result));
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<SockView>> uploadSockFile(@RequestParam("file") MultipartFile file) {
        List<SockView> sockViews = sockService.uploadSocksFromCsv(file);
        return ResponseEntity.ok().body(sockViews);
    }

    @GetMapping
    public ResponseEntity<Long> getSockCount(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String cottonPercentageOperator,
            @RequestParam(required = false) Double minCottonPercentage,
            @RequestParam(required = false) Double maxCottonPercentage,
            @RequestParam(required = false) Double cottonPercentage) {

        if (color != null && color.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid color parameter");
        }

        if (cottonPercentageOperator != null && cottonPercentage == null && !cottonPercentageOperator.equals("between")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cotton percentage is required for comparison");
        }

        if (cottonPercentageOperator != null && cottonPercentageOperator.equals("between")) {
            if (cottonPercentage == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cotton percentage must be empty if between operator used");
            }
            if (minCottonPercentage == null || maxCottonPercentage == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minCottonPercentage and maxCottonPercentage are required if between operator used");
            }
        }

        long count = sockService.getFilteredSocksCount(color, cottonPercentageOperator, cottonPercentage, minCottonPercentage, maxCottonPercentage);
        return ResponseEntity.ok(count);
    }
}
