package com.example.capsuletoy.service.scrapeConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.capsuletoy.domain.crone.CroneValidater;
import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;

@Component
public class ScrapeConfigUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(ScrapeConfigService.class);

    @Autowired
    private ScrapeConfigService scrapeConfigService;

    @Autowired
    private CroneValidater croneValidater;

    @Autowired
    private ScrapeConfigRepository scrapeConfigRepository;

    /**
     * 設定を更新
     */
    public ScrapeConfig updateConfig(Long id, ScrapeConfig updatedConfig) {
        ScrapeConfig existing = scrapeConfigService.getConfigById(id);

        // cron式のバリデーション
        if (updatedConfig.getCronExpression() != null && !updatedConfig.getCronExpression().isEmpty()) {
            croneValidater.validateCronExpression(updatedConfig.getCronExpression());
        }

        existing.setSiteName(updatedConfig.getSiteName());
        existing.setSiteUrl(updatedConfig.getSiteUrl());
        existing.setCronExpression(updatedConfig.getCronExpression());
        existing.setIsEnabled(updatedConfig.getIsEnabled());

        logger.info("スクレイピング設定を更新: ID={}, サイト={}", id, existing.getSiteName());
        return scrapeConfigRepository.save(existing);
    }

    /**
     * 有効/無効を切り替え
     */
    public ScrapeConfig toggleEnabled(Long id) {
        ScrapeConfig config = scrapeConfigService.getConfigById(id);
        config.setIsEnabled(!config.getIsEnabled());
        logger.info("スクレイピング設定の有効/無効を切り替え: ID={}, サイト={}, 有効={}",
                id, config.getSiteName(), config.getIsEnabled());
        return scrapeConfigRepository.save(config);
    }
}
