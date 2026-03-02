package com.example.capsuletoy.service.scrapeConfig;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScrapeConfigService {
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
}
