package com.app.backspark.services;

import com.app.backspark.models.Sock;
import com.app.backspark.views.SockView;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface SockService {
    @Transactional(rollbackFor = Exception.class)
    List<Sock> registerIncome(Set<SockView> socksToSave);

    @Transactional(rollbackFor = Exception.class)
    Sock registerOutcome(SockView sock);

    @Transactional(readOnly = true)
    long getFilteredSocksCount(String color, String cottonPercentageOperator, Double cottonPercentage, Double minCottonPercentage, Double maxCottonPercentage);

    @Transactional(rollbackFor = Exception.class)
    Sock updateSock(Long id, SockView sock);

    @Transactional(rollbackFor = Exception.class)
    List<SockView> uploadSocksFromCsv(MultipartFile file);
}
