package com.example.capsuletoy.runner;

import com.example.capsuletoy.domain.configAdmin.ScrapingConfigChecker;
import com.example.capsuletoy.domain.product.NewFlagsAdmin;
import com.example.capsuletoy.domain.scraping.RegularScrapeExecuter;
import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.service.notification.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GitHub Actions用のスクレイピング実行ランナー
 * scrapeプロファイル時のみ有効。起動時にスクレイピングを実行して終了する。
 */
@Component
@Profile("scrape")
public class ScrapeCommandRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeCommandRunner.class);

    @Autowired
    private ScrapingConfigChecker scrapingConfigChecker;

    @Autowired
    private NewFlagsAdmin newFlagsAdmin;

    @Autowired
    private RegularScrapeExecuter scrapeExecuter;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== GitHub Actions スクレイピング開始 ===");

        try {
            // 古い新着フラグをリセット（30日経過）
            newFlagsAdmin.resetOldNewFlags(30);
            logger.info("新着フラグリセット完了");

            // 有効な設定を取得
            List<ScrapeConfig> enabledConfigs = scrapingConfigChecker.checkEnabledConfig();

            // スクレイピング実行
            List<Product> allNewProducts = scrapeExecuter.executeScraping(enabledConfigs);

            // 通知メール送信
            notificationService.sendFinishedEmail(allNewProducts);

            logger.info("=== GitHub Actions スクレイピング完了（新着: {}件） ===", allNewProducts.size());
        } catch (Exception e) {
            logger.error("スクレイピング実行中にエラーが発生: {}", e.getMessage(), e);
            throw e;
        }
    }
}
