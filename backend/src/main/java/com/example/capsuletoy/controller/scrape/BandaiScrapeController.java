package com.example.capsuletoy.controller.scrape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.record.ScrapeResult;
import com.example.capsuletoy.repository.ProductRepository;
import com.example.capsuletoy.scraper.BandaiScraper;
import com.example.capsuletoy.service.notification.NotificationService;
import com.example.capsuletoy.service.scraping.ScrapeService;

@RestController
@RequestMapping("/api/scrape")
public class BandaiScrapeController {
    private static final Logger logger = LoggerFactory.getLogger(BandaiScrapeController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BandaiScraper bandaiScraper;

    @Autowired
    private ScrapeService scrapeService;

    @Autowired
    private NotificationService notificationService;

    /**
     * バンダイサイトのスクレイピングを手動実行
     * POST /api/scrape/bandai
     */
    @PostMapping("/bandai")
    public ResponseEntity<?> scrapeBandai() {
        logger.info("Manual scraping requested for Bandai");

        try {
            ScrapeResult result = scrapeService.executeScraping(bandaiScraper, "BANDAI_GASHAPON");

            // スクレイピング完了後に通知を送信（新着0件でも送信）
            List<Product> newProducts = productRepository.findByIsNewTrueAndManufacturer("BANDAI");
            notificationService.sendFinishedEmail(newProducts);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("site", "BANDAI_GASHAPON");
            response.put("totalProducts", result.totalProducts());
            response.put("newProducts", result.newProducts());
            response.put("message", "全取得: " + result.totalProducts() + "件、うち新着: " + result.newProducts() + "件");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Scraping failed for Bandai: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("site", "BANDAI_GASHAPON");
            errorResponse.put("message", "スクレイピングに失敗しました: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
