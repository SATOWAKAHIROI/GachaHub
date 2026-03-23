package com.example.capsuletoy.domain.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.capsuletoy.model.ScrapeLog;
import com.example.capsuletoy.repository.ScrapeLogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScrapeLogAdministrater {
    @Autowired
    private ScrapeLogRepository scrapeLogRepository;


    public ScrapeLog getDefaultScrapeLog(String targetSite){
        ScrapeLog scrapeLog = new ScrapeLog();
        scrapeLog.setTargetSite(targetSite);
        scrapeLog.setExecutedAt(LocalDateTime.now());
        return scrapeLog;
    }

    public ScrapeLog setScrapeLog(ScrapeLog scrapeLog, String status, int productsCount, String errorMessage){
        scrapeLog.setStatus(status);
        scrapeLog.setProductsFound(productsCount);
        scrapeLog.setErrorMessage(errorMessage);
        return scrapeLog;
    }

    /**
     * スクレイピング履歴を取得
     */
    public List<ScrapeLog> getScrapeHistory() {
        return scrapeLogRepository.findAll();
    }

    /**
     * 最新のスクレイピングログを取得
     */
    public List<ScrapeLog> getRecentScrapeLogs(int limit) {
        return scrapeLogRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getExecutedAt().compareTo(a.getExecutedAt()))
                .limit(limit)
                .toList();
    }

    /**
     * 特定サイトのスクレイピング履歴を取得
     */
    public List<ScrapeLog> getScrapeLogsByTargetSite(String targetSite) {
        return scrapeLogRepository.findAll()
                .stream()
                .filter(log -> log.getTargetSite().equals(targetSite))
                .sorted((a, b) -> b.getExecutedAt().compareTo(a.getExecutedAt()))
                .toList();
    }
}
