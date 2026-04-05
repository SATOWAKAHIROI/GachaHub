package com.example.capsuletoy.config;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ScrapeConfigInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeConfigInitializer.class);

    @Autowired
    private ScrapeConfigRepository scrapeConfigRepository;

    @Override
    public void run(ApplicationArguments args) {
        createConfigIfNotExists("BANDAI", "https://gashapon.jp/products/", "0 0 6 * * *");
        createConfigIfNotExists("TAKARA_TOMY", "https://www.takaratomy-arts.co.jp/items/gacha/calendar/", "0 0 6 * * *");
    }

    private void createConfigIfNotExists(String siteName, String siteUrl, String cronExpression) {
        if (scrapeConfigRepository.findBySiteName(siteName) == null) {
            ScrapeConfig config = new ScrapeConfig();
            config.setSiteName(siteName);
            config.setSiteUrl(siteUrl);
            config.setCronExpression(cronExpression);
            config.setIsEnabled(true);
            scrapeConfigRepository.save(config);
            logger.info("スクレイピング設定を作成しました: {}", siteName);
        } else {
            logger.info("スクレイピング設定は既に存在します: {}", siteName);
        }
    }
}
