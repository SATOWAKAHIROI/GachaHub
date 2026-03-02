package com.example.capsuletoy.service.scraping;

import com.example.capsuletoy.domain.log.ScrapeLogAdministrater;
import com.example.capsuletoy.domain.scraping.ManualScrapeExecuter;
import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.ScrapeLog;
import com.example.capsuletoy.record.ScrapeCore;
import com.example.capsuletoy.record.ScrapeResult;
import com.example.capsuletoy.repository.ScrapeLogRepository;
import com.example.capsuletoy.scraper.BaseScraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScrapeService {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeService.class);

    @Autowired
    private ManualScrapeExecuter manualScrapeExecuter;

    @Autowired
    private ScrapeLogRepository scrapeLogRepository;

    @Autowired
    private ScrapeLogAdministrater logAdministrater;

    /**
     * スクレイピングを実行してデータベースに保存
     *
     * @param scraper スクレイパーインスタンス
     * @param targetSite 対象サイト名
     * @return 全取得商品数と新着商品数
     */
    @Transactional
    public ScrapeResult executeScraping(BaseScraper scraper, String targetSite) {
        ScrapeCore result = executeScrapeCore(scraper, targetSite);

        int totalCount = result.totalCount();
        List<Product> newProducts = result.newProducts();

        int newCount = newProducts.size();

        return new ScrapeResult(totalCount, newCount);
    }

    /**
     * スクレイピングを実行して新着商品リストを返す（通知用）
     *
     * @param scraper スクレイパーインスタンス
     * @param targetSite 対象サイト名
     * @return 新規保存された商品リスト
     */
    @Transactional
    public List<Product> executeScrapingWithNewProducts(BaseScraper scraper, String targetSite) {
        ScrapeCore result = executeScrapeCore(scraper, targetSite);

        List<Product> newProducts = result.newProducts();


        return newProducts;
    }

    private ScrapeCore executeScrapeCore(BaseScraper scraper, String targetSite){
        logger.info("Starting scraping for: {}", targetSite);

        ScrapeLog scrapeLog = logAdministrater.getDefaultScrapeLog(targetSite);

        int totalCount = 0;
        List<Product> newProducts = new ArrayList<>();

        try {
            List<Product> scrapedProducts = manualScrapeExecuter.scrapeProducts(scraper);
            totalCount = scrapedProducts.size();

            newProducts = manualScrapeExecuter.getNewProductList(scrapedProducts);
            int newCount = newProducts.size();

            logAdministrater.setScrapeLog(scrapeLog, "SUCCESS", totalCount, null);

            logger.info("Scraping completed for {}: {} products found, {} new", targetSite, totalCount, newCount);

        } catch (Exception e) {
            logAdministrater.setScrapeLog(scrapeLog, "FAILURE", 0, e.getMessage());

            logger.error("Scraping failed for {}: {}", targetSite, e.getMessage(), e);
        } finally {
            scrapeLogRepository.save(scrapeLog);
        }

        return new ScrapeCore(totalCount, newProducts);
    }
}
