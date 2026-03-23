package com.example.capsuletoy.domain.configAdmin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;
import com.example.capsuletoy.service.scheduled.ScheduledScrapeService;

@Component
public class ScrapingConfigChecker {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledScrapeService.class);

    @Autowired
    ScrapeConfigRepository scrapeConfigRepository;

    public List<ScrapeConfig> checkEnabledConfig(){
        List<ScrapeConfig> enabledConfigs = scrapeConfigRepository.findByIsEnabledTrue();

        if (enabledConfigs.isEmpty()) {
            logger.info("有効なスクレイピング設定がありません。スキップします。");
            return List.of();
        }

        return enabledConfigs;
    }
}
