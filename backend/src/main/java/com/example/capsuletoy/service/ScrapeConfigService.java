package com.example.capsuletoy.service;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScrapeConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeConfigService.class);

    @Autowired
    private ScrapeConfigRepository scrapeConfigRepository;

    /**
     * 全設定を取得
     */
    public List<ScrapeConfig> getAllConfigs() {
        return scrapeConfigRepository.findAll();
    }

    /**
     * 有効な設定のみ取得
     */
    public List<ScrapeConfig> getEnabledConfigs() {
        return scrapeConfigRepository.findByIsEnabledTrue();
    }

    /**
     * IDで設定を取得
     */
    public ScrapeConfig getConfigById(Long id) {
        return scrapeConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("スクレイピング設定が見つかりません: ID=" + id));
    }

    /**
     * サイト名で設定を取得
     */
    public ScrapeConfig getConfigBySiteName(String siteName) {
        return scrapeConfigRepository.findBySiteName(siteName);
    }

    /**
     * 新規設定を作成
     */
    public ScrapeConfig createConfig(ScrapeConfig config) {
        // cron式のバリデーション
        if (config.getCronExpression() != null && !config.getCronExpression().isEmpty()) {
            validateCronExpression(config.getCronExpression());
        }

        // 同名サイトの重複チェック
        ScrapeConfig existing = scrapeConfigRepository.findBySiteName(config.getSiteName());
        if (existing != null) {
            throw new RuntimeException("同じサイト名の設定が既に存在します: " + config.getSiteName());
        }

        logger.info("スクレイピング設定を作成: {}", config.getSiteName());
        return scrapeConfigRepository.save(config);
    }

    /**
     * 設定を更新
     */
    public ScrapeConfig updateConfig(Long id, ScrapeConfig updatedConfig) {
        ScrapeConfig existing = getConfigById(id);

        // cron式のバリデーション
        if (updatedConfig.getCronExpression() != null && !updatedConfig.getCronExpression().isEmpty()) {
            validateCronExpression(updatedConfig.getCronExpression());
        }

        existing.setSiteName(updatedConfig.getSiteName());
        existing.setSiteUrl(updatedConfig.getSiteUrl());
        existing.setCronExpression(updatedConfig.getCronExpression());
        existing.setIsEnabled(updatedConfig.getIsEnabled());

        logger.info("スクレイピング設定を更新: ID={}, サイト={}", id, existing.getSiteName());
        return scrapeConfigRepository.save(existing);
    }

    /**
     * 設定を削除
     */
    public void deleteConfig(Long id) {
        ScrapeConfig config = getConfigById(id);
        logger.info("スクレイピング設定を削除: ID={}, サイト={}", id, config.getSiteName());
        scrapeConfigRepository.deleteById(id);
    }

    /**
     * 有効/無効を切り替え
     */
    public ScrapeConfig toggleEnabled(Long id) {
        ScrapeConfig config = getConfigById(id);
        config.setIsEnabled(!config.getIsEnabled());
        logger.info("スクレイピング設定の有効/無効を切り替え: ID={}, サイト={}, 有効={}",
                id, config.getSiteName(), config.getIsEnabled());
        return scrapeConfigRepository.save(config);
    }

    /**
     * cron式のバリデーション
     */
    private void validateCronExpression(String cronExpression) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("無効なcron式です: " + cronExpression);
        }
    }
}
