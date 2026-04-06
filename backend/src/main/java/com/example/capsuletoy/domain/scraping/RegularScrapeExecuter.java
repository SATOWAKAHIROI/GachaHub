package com.example.capsuletoy.domain.scraping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;
import com.example.capsuletoy.scraper.BandaiScraper;
import com.example.capsuletoy.scraper.BaseScraper;
import com.example.capsuletoy.scraper.TakaraTomyScraper;
import com.example.capsuletoy.service.scheduled.ScheduledScrapeService;
import com.example.capsuletoy.service.scraping.ScrapeService;

@Component
public class RegularScrapeExecuter {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledScrapeService.class);

    @Autowired
    private ScrapeConfigRepository scrapeConfigRepository;

    @Autowired
    private BandaiScraper bandaiScraper;

    @Autowired
    private TakaraTomyScraper takaraTomyScraper;

    @Autowired
    private ScrapeService scrapeService;

    public List<Product> executeScraping(List<ScrapeConfig> enabledConfigs){
        List<Product> allNewProducts = new ArrayList<>();

        for (ScrapeConfig config : enabledConfigs) {

            List<Product> newProducts = scrapeNewProducts(config);
            allNewProducts.addAll(newProducts);

            // 最終実行日時を更新
            config.setLastScrapedAt(LocalDateTime.now());
            scrapeConfigRepository.save(config);

            logger.info("スクレイピング完了: {} - {}件の新着商品", config.getSiteName(), newProducts.size());
        }

        return allNewProducts;
    }

    private List<Product> scrapeNewProducts(ScrapeConfig config){
        BaseScraper scraper = getScraperForSite(config.getSiteName());
        if (scraper == null) {
            logger.warn("未対応のサイト: {}", config.getSiteName());
            return List.of();
        }

        try {
            logger.info("スクレイピング実行: {} ({})", config.getSiteName(), config.getSiteUrl());

            List<Product> newProducts = scrapeService.executeScrapingWithNewProducts(scraper, config.getSiteName());
            return newProducts;

        } catch (Exception e) {
            logger.error("スクレイピング失敗: {} - {}", config.getSiteName(), e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * サイト名に対応するスクレイパーを取得
     */
    private BaseScraper getScraperForSite(String siteName) {
        return switch (siteName) {
            case "BANDAI" -> bandaiScraper;
            case "TAKARA_TOMY" -> takaraTomyScraper;
            default -> null;
        };
    }
}
