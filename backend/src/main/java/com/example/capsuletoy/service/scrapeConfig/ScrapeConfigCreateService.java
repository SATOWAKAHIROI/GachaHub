package com.example.capsuletoy.service.scrapeConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.capsuletoy.domain.crone.CroneValidater;
import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;

@Service
public class ScrapeConfigCreateService {
    private static final Logger logger = LoggerFactory.getLogger(ScrapeConfigService.class);

    @Autowired
    private ScrapeConfigRepository scrapeConfigRepository;

    @Autowired
    private CroneValidater croneValidater;

    /**
     * 新規設定を作成
     */
    public ScrapeConfig createConfig(ScrapeConfig config) {
        // cron式のバリデーション
        if (config.getCronExpression() != null && !config.getCronExpression().isEmpty()) {
            croneValidater.validateCronExpression(config.getCronExpression());
        }

        // 同名サイトの重複チェック
        ScrapeConfig existing = scrapeConfigRepository.findBySiteName(config.getSiteName());
        if (existing != null) {
            throw new RuntimeException("同じサイト名の設定が既に存在します: " + config.getSiteName());
        }

        logger.info("スクレイピング設定を作成: {}", config.getSiteName());
        return scrapeConfigRepository.save(config);
    }
}
