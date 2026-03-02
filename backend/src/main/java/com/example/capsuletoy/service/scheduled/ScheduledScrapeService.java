package com.example.capsuletoy.service.scheduled;

import com.example.capsuletoy.domain.configAdmin.ScrapingConfigChecker;
import com.example.capsuletoy.domain.product.NewFlagsAdmin;
import com.example.capsuletoy.domain.scraping.RegularScrapeExecuter;
import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.service.notification.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定期スクレイピング実行サービス
 * ScrapeConfigテーブルの有効な設定に基づき、定期的にスクレイピングを実行する
 */
@Service
public class ScheduledScrapeService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledScrapeService.class);

    @Autowired
    private ScrapingConfigChecker scrapingConfigChecker;

    @Autowired
    NewFlagsAdmin newFlagsAdmin;

    @Autowired
    RegularScrapeExecuter scrapeExecuter;

    @Autowired
    private NotificationService notificationService;

    /**
     * 定期スクレイピング実行（デフォルト: 毎日午前6時に実行）
     * スクレイピング後、新着商品があれば通知メールを送信する
     * cron式: 秒 分 時 日 月 曜日
     */
    @Scheduled(cron = "${scraping.schedule.cron:0 0 6 * * *}")
    public void executeScheduledScraping() {
        logger.info("=== 定期スクレイピング開始 ===");

        //有効な設定を取得
        List<ScrapeConfig> enabledConfigs = scrapingConfigChecker.checkEnabledConfig();

        //有効な設定を元に商品情報をスクレイピング
        List<Product> allNewProducts = scrapeExecuter.executeScraping(enabledConfigs);

        // スクレイピング完了後に通知メールを送信（新着0件でも送信）
        notificationService.sendFinishedEmail(allNewProducts);

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
            newFlagsAdmin.resetOldNewFlags(30);
            logger.info("新着フラグリセット完了");
        } catch (Exception e) {
            logger.error("新着フラグリセット失敗: {}", e.getMessage(), e);
        }
    }
}
