package com.example.capsuletoy.service;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;
import com.example.capsuletoy.scraper.BandaiScraper;
import com.example.capsuletoy.scraper.BaseScraper;
import com.example.capsuletoy.scraper.TakaraTomyScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 定期スクレイピング実行サービス
 * ScrapeConfigテーブルの有効な設定に基づき、定期的にスクレイピングを実行する
 */
@Service
public class ScheduledScrapeService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledScrapeService.class);

    @Autowired
    private ScrapeConfigRepository scrapeConfigRepository;

    @Autowired
    private ScrapeService scrapeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BandaiScraper bandaiScraper;

    @Autowired
    private TakaraTomyScraper takaraTomyScraper;

    /**
     * 定期スクレイピング実行（デフォルト: 毎日午前6時に実行）
     * スクレイピング後、新着商品があれば通知メールを送信する
     * cron式: 秒 分 時 日 月 曜日
     */
    @Scheduled(cron = "${scraping.schedule.cron:0 0 6 * * *}")
    public void executeScheduledScraping() {
        logger.info("=== 定期スクレイピング開始 ===");

        List<ScrapeConfig> enabledConfigs = scrapeConfigRepository.findByIsEnabledTrue();

        if (enabledConfigs.isEmpty()) {
            logger.info("有効なスクレイピング設定がありません。スキップします。");
            return;
        }

        List<Product> allNewProducts = new ArrayList<>();

        for (ScrapeConfig config : enabledConfigs) {
            try {
                logger.info("スクレイピング実行: {} ({})", config.getSiteName(), config.getSiteUrl());

                BaseScraper scraper = getScraperForSite(config.getSiteName());
                if (scraper == null) {
                    logger.warn("未対応のサイト: {}", config.getSiteName());
                    continue;
                }

                List<Product> newProducts = scrapeService.executeScrapingWithNewProducts(scraper, config.getSiteName());
                allNewProducts.addAll(newProducts);

                // 最終実行日時を更新
                config.setLastScrapedAt(LocalDateTime.now());
                scrapeConfigRepository.save(config);

                logger.info("スクレイピング完了: {} - {}件の新着商品", config.getSiteName(), newProducts.size());

            } catch (Exception e) {
                logger.error("スクレイピング失敗: {} - {}", config.getSiteName(), e.getMessage(), e);
            }
        }

        // スクレイピング完了後に通知メールを送信（新着0件でも送信）
        logger.info("通知メールを送信します（新着{}件）", allNewProducts.size());
        try {
            notificationService.notifyNewProducts(allNewProducts);
        } catch (Exception e) {
            logger.error("通知メール送信中にエラー: {}", e.getMessage(), e);
        }

        logger.info("=== 定期スクレイピング終了 ===");
    }

    /**
     * 古い新着フラグをリセット（デフォルト: 毎日午前0時に実行）
     * 30日以上経過した商品のis_newフラグをfalseにする
     */
    @Scheduled(cron = "${scraping.reset-new-flag.cron:0 0 0 * * *}")
    public void resetOldNewFlags() {
        logger.info("=== 新着フラグリセット開始 ===");
        try {
            productService.resetOldNewFlags(30);
            logger.info("新着フラグリセット完了");
        } catch (Exception e) {
            logger.error("新着フラグリセット失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * サイト名に対応するスクレイパーを取得
     */
    private BaseScraper getScraperForSite(String siteName) {
        return switch (siteName) {
            case "BANDAI" -> bandaiScraper;
            case "TAKARA_TOMY_ARTS" -> takaraTomyScraper;
            default -> null;
        };
    }
}
