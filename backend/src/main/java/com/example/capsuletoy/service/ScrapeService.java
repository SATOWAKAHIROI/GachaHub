package com.example.capsuletoy.service;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.ScrapeLog;
import com.example.capsuletoy.repository.ScrapeLogRepository;
import com.example.capsuletoy.scraper.BaseScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScrapeService {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeService.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ScrapeLogRepository scrapeLogRepository;

    /**
     * スクレイピングを実行してデータベースに保存
     *
     * @param scraper スクレイパーインスタンス
     * @param targetSite 対象サイト名
     * @return 保存された商品数
     */
    @Transactional
    public int executeScraping(BaseScraper scraper, String targetSite) {
        List<Product> newProducts = executeScrapingWithNewProducts(scraper, targetSite);
        return newProducts.size();
    }

    /**
     * スクレイピングを実行して新着商品リストを返す
     *
     * @param scraper スクレイパーインスタンス
     * @param targetSite 対象サイト名
     * @return 新規保存された商品リスト
     */
    @Transactional
    public List<Product> executeScrapingWithNewProducts(BaseScraper scraper, String targetSite) {
        logger.info("Starting scraping for: {}", targetSite);

        ScrapeLog scrapeLog = new ScrapeLog();
        scrapeLog.setTargetSite(targetSite);
        scrapeLog.setExecutedAt(LocalDateTime.now());

        List<Product> newProducts = new ArrayList<>();

        try {
            // スクレイピング実行
            List<Product> scrapedProducts = scraper.scrape();

            // 商品をデータベースに保存
            for (Product product : scrapedProducts) {
                try {
                    Product saved = productService.saveScrapedProduct(product);
                    if (saved.getIsNew() != null && saved.getIsNew()) {
                        newProducts.add(saved);
                    }
                } catch (Exception e) {
                    logger.error("Error saving product: {}", product.getProductName(), e);
                }
            }

            // スクレイピング成功ログ
            scrapeLog.setStatus("SUCCESS");
            scrapeLog.setProductsFound(scrapedProducts.size());
            scrapeLog.setErrorMessage(null);

            logger.info("Scraping completed for {}: {} products found, {} new",
                    targetSite, scrapedProducts.size(), newProducts.size());

        } catch (Exception e) {
            // スクレイピング失敗ログ
            scrapeLog.setStatus("FAILURE");
            scrapeLog.setProductsFound(0);
            scrapeLog.setErrorMessage(e.getMessage());

            logger.error("Scraping failed for {}: {}", targetSite, e.getMessage(), e);
        } finally {
            // ログを保存
            scrapeLogRepository.save(scrapeLog);
        }

        return newProducts;
    }

    /**
     * スクレイピング履歴を取得
     */
    public List<ScrapeLog> getScrapeHistory() {
        return scrapeLogRepository.findAll();
    }

    /**
     * 最新のスクレイピングログを取得
     */
    public List<ScrapeLog> getRecentScrapeLogs(int limit) {
        return scrapeLogRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getExecutedAt().compareTo(a.getExecutedAt()))
                .limit(limit)
                .toList();
    }

    /**
     * 特定サイトのスクレイピング履歴を取得
     */
    public List<ScrapeLog> getScrapeLogsByTargetSite(String targetSite) {
        return scrapeLogRepository.findAll()
                .stream()
                .filter(log -> log.getTargetSite().equals(targetSite))
                .sorted((a, b) -> b.getExecutedAt().compareTo(a.getExecutedAt()))
                .toList();
    }
}
