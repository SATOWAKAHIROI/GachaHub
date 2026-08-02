package com.example.capsuletoy.service.scrapeConfig;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;

@Service
public class ScrapeConfigDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(ScrapeConfigDeleteService.class);

    private final ScrapeConfigService scrapeConfigService;

    private final ScrapeConfigRepository scrapeConfigRepository;

    public ScrapeConfigDeleteService(ScrapeConfigService scrapeConfigService,
            ScrapeConfigRepository scrapeConfigRepository) {
        this.scrapeConfigService = scrapeConfigService;
        this.scrapeConfigRepository = scrapeConfigRepository;
    }

    /**
     * 設定を削除
     */
    public void deleteConfig(Long id) {
        ScrapeConfig config = scrapeConfigService.getConfigById(id);
        logger.info("スクレイピング設定を削除: ID={}, サイト={}", id, config.getSiteName());
        scrapeConfigRepository.deleteById(id);
    }
}
