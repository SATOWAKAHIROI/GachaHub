package com.example.capsuletoy.scraper;

import com.example.capsuletoy.model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * バンダイガシャポン公式サイトのスクレイパー
 * 詳細ページから発売日を含む商品情報を取得
 */
@Component
public class BandaiScraper extends BaseScraper {

    private static final Logger logger = LoggerFactory.getLogger(BandaiScraper.class);
    private static final String BASE_URL = "https://gashapon.jp";
    private static final String TARGET_URL = "https://gashapon.jp/products/";

    // 最大処理件数（タイムアウト防止のため制限）
    private static final int MAX_PRODUCTS = 50;

    // 重複チェック用
    private Set<String> processedUrls;

    @Override
    protected String getTargetUrl() {
        return TARGET_URL;
    }

    @Override
    protected String getManufacturerName() {
        return "BANDAI";
    }

    /**
     * スクレイピング実行（オーバーライド）
     * 詳細ページに遷移して発売日を取得
     */
    @Override
    public List<Product> scrape() {
        List<Product> products = new ArrayList<>();
        processedUrls = new HashSet<>();

        try {
            // WebDriver初期化
            driver = scraperConfig.createChromeDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            logger.info("Starting scraping for: {}", getManufacturerName());

            // 一覧ページにアクセス
            driver.get(getTargetUrl());
            waitForPageLoad();

            // 商品URLを収集（詳細ページ遷移前に）
            List<WebElement> linkElements = findElementsSafely(By.tagName("a"));
            logger.info("Found {} link elements on page", linkElements.size());

            List<String> productUrls = new ArrayList<>();
            for (WebElement linkElement : linkElements) {
                try {
                    String href = getElementAttribute(linkElement, "href");
                    if (href != null && href.contains("detail.php?jan_code=")) {
                        if (!processedUrls.contains(href)) {
                            productUrls.add(href);
                            processedUrls.add(href);
                        }
                    }
                } catch (Exception e) {
                    // StaleElementReferenceExceptionを無視
                }
            }
            logger.info("Found {} unique product links", productUrls.size());

            // 各詳細ページにアクセスして商品情報を取得
            for (String productUrl : productUrls) {
                if (products.size() >= MAX_PRODUCTS) {
                    logger.info("Reached max product limit ({}), stopping", MAX_PRODUCTS);
                    break;
                }

                try {
                    Product product = scrapeProductDetail(productUrl);
                    if (product != null) {
                        products.add(product);
                        logger.info("Scraped product #{}: {}", products.size(), product.getProductName());

                        if (products.size() % 10 == 0) {
                            logger.info("Progress: {} products scraped", products.size());
                        }
                    }

                    // サイトへの負荷軽減
                    Thread.sleep(500);
                } catch (Exception e) {
                    logger.warn("Failed to scrape product from {}: {}", productUrl, e.getMessage());
                }
            }

            logger.info("Scraped {} products total from {}", products.size(), getManufacturerName());

        } catch (Exception e) {
            logger.error("Error during scraping for {}: {}", getManufacturerName(), e.getMessage(), e);
        } finally {
            // WebDriver終了
            scraperConfig.quitDriver(driver);
        }

        return products;
    }

    @Override
    protected void waitForPageLoad() {
        try {
            Thread.sleep(3000);
            waitForElement(By.tagName("a"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected List<Product> scrapeProducts() {
        // scrape()をオーバーライドしているため、このメソッドは直接呼ばれない
        return new ArrayList<>();
    }

    /**
     * 商品詳細ページから商品情報を取得
     */
    private Product scrapeProductDetail(String detailUrl) {
        try {
            driver.get(detailUrl);
            Thread.sleep(2000);

            // 商品名を取得
            String productName = extractProductName();
            if (productName == null || productName.isEmpty()) {
                logger.warn("Product name not found for URL: {}", detailUrl);
                return null;
            }

            // 画像URLを取得
            String imageUrl = extractImageUrl();

            // 価格を取得
            Integer price = extractPrice();

            // 発売日を取得
            LocalDate releaseDate = extractReleaseDate();

            // 商品説明を取得
            String description = extractDescription();

            // Productオブジェクトを作成
            Product product = new Product();
            product.setProductName(productName);
            product.setManufacturer(getManufacturerName());
            product.setImageUrl(imageUrl);
            product.setPrice(price);
            product.setReleaseDate(releaseDate);
            product.setSourceUrl(detailUrl);
            product.setDescription(description);
            product.setIsNew(true);

            return product;

        } catch (Exception e) {
            logger.warn("Failed to scrape product detail from {}: {}", detailUrl, e.getMessage());
            return null;
        }
    }

    /**
     * 商品名を抽出（h1タグから）
     */
    private String extractProductName() {
        try {
            // h1タグから商品名を取得
            List<WebElement> h1Elements = findElementsSafely(By.tagName("h1"));
            for (WebElement h1 : h1Elements) {
                String text = getElementText(h1);
                if (text != null && !text.isEmpty()) {
                    return text.trim();
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract product name: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 画像URLを抽出
     */
    private String extractImageUrl() {
        try {
            List<WebElement> imgElements = findElementsSafely(By.tagName("img"));
            for (WebElement img : imgElements) {
                String src = getElementAttribute(img, "src");
                // バンダイCDNの商品画像を探す
                if (src != null && src.contains("bandai-a.akamaihd.net") && src.contains("/model/")) {
                    return src;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract image URL: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 価格を抽出
     */
    private Integer extractPrice() {
        try {
            String pageText = driver.findElement(By.tagName("body")).getText();

            // "300円（税込）" のような形式から数値を抽出
            Pattern pattern = Pattern.compile("(\\d+)円[（(]税込[）)]");
            Matcher matcher = pattern.matcher(pageText);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }

            // フォールバック: "300円" の形式
            Pattern fallbackPattern = Pattern.compile("(\\d+)円");
            Matcher fallbackMatcher = fallbackPattern.matcher(pageText);
            if (fallbackMatcher.find()) {
                return Integer.parseInt(fallbackMatcher.group(1));
            }
        } catch (Exception e) {
            logger.warn("Failed to extract price: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 発売日を抽出
     * 形式例:
     * - "2026年2月 第2週" → 第2週の最初の日曜日
     * - "2026年5月未定" → 月の最初の日
     * - "2026年5月" → 月の最初の日
     */
    private LocalDate extractReleaseDate() {
        try {
            String pageText = driver.findElement(By.tagName("body")).getText();

            // "2026年2月 第2週" のような形式から年月と週を抽出
            Pattern weekPattern = Pattern.compile("(\\d{4})年(\\d{1,2})月\\s*第(\\d)週");
            Matcher weekMatcher = weekPattern.matcher(pageText);

            if (weekMatcher.find()) {
                int year = Integer.parseInt(weekMatcher.group(1));
                int month = Integer.parseInt(weekMatcher.group(2));
                int week = Integer.parseInt(weekMatcher.group(3));

                // 第N週の最初の日曜日を計算
                return getFirstSundayOfWeek(year, month, week);
            }

            // "2026年5月" または "2026年5月未定" のような形式から年月を抽出
            Pattern monthPattern = Pattern.compile("(\\d{4})年(\\d{1,2})月");
            Matcher monthMatcher = monthPattern.matcher(pageText);

            if (monthMatcher.find()) {
                int year = Integer.parseInt(monthMatcher.group(1));
                int month = Integer.parseInt(monthMatcher.group(2));

                // 月の最初の日を返す
                return LocalDate.of(year, month, 1);
            }
        } catch (Exception e) {
            logger.warn("Failed to extract release date: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 指定された月の第N週の最初の日曜日を取得
     * 第1週: 1日〜7日の範囲
     * 第2週: 8日〜14日の範囲
     * 第3週: 15日〜21日の範囲
     * 第4週: 22日〜28日の範囲
     */
    private LocalDate getFirstSundayOfWeek(int year, int month, int week) {
        // 週の開始日を計算（第1週=1日、第2週=8日、第3週=15日、第4週=22日）
        int startDay = (week - 1) * 7 + 1;
        LocalDate startDate = LocalDate.of(year, month, startDay);

        // その週の範囲内で最初の日曜日を探す
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            if (date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                return date;
            }
        }

        // 日曜日が見つからない場合は週の開始日を返す（通常はありえない）
        return startDate;
    }

    /**
     * 商品説明を抽出
     */
    private String extractDescription() {
        try {
            String pageText = driver.findElement(By.tagName("body")).getText();

            StringBuilder description = new StringBuilder("バンダイガシャポン公式サイトより");

            // "全X種" のようなラインナップ情報を探す
            Pattern lineupPattern = Pattern.compile("全(\\d+)種");
            Matcher lineupMatcher = lineupPattern.matcher(pageText);
            if (lineupMatcher.find()) {
                description.append(" - 全").append(lineupMatcher.group(1)).append("種");
            }

            return description.toString();

        } catch (Exception e) {
            logger.warn("Failed to extract description: {}", e.getMessage());
            return "バンダイガシャポン公式サイトより";
        }
    }
}
