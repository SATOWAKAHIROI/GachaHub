package com.example.capsuletoy.scraper;

import com.example.capsuletoy.model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * タカラトミーアーツ公式サイトのスクレイパー
 */
@Component
public class TakaraTomyScraper extends BaseScraper {

    private static final Logger logger = LoggerFactory.getLogger(TakaraTomyScraper.class);
    private static final String BASE_URL = "https://www.takaratomy-arts.co.jp";
    private static final String TARGET_URL = "https://www.takaratomy-arts.co.jp/items/gacha.html";

    @Override
    protected String getTargetUrl() {
        return TARGET_URL;
    }

    @Override
    protected String getManufacturerName() {
        return "TAKARA_TOMY";
    }

    @Override
    protected void waitForPageLoad() {
        try {
            // 商品一覧が読み込まれるまで待機
            Thread.sleep(3000);

            // 商品リンクが表示されるまで待機
            waitForElement(By.tagName("a"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected List<Product> scrapeProducts() {
        List<Product> products = new ArrayList<>();
        Set<String> processedUrls = new HashSet<>();

        try {
            // 全てのリンク要素を取得
            List<WebElement> linkElements = findElementsSafely(By.tagName("a"));

            logger.info("Found {} link elements", linkElements.size());

            for (WebElement linkElement : linkElements) {
                try {
                    String href = getElementAttribute(linkElement, "href");

                    // /items/item.html?n= を含むリンクのみ処理
                    if (href != null && href.contains("/items/item.html?n=")) {
                        // 重複チェック
                        if (processedUrls.contains(href)) {
                            continue;
                        }
                        processedUrls.add(href);

                        // 詳細ページにアクセスして商品情報を取得
                        Product product = scrapeProductDetail(href);
                        if (product != null) {
                            products.add(product);
                        }

                        // サイトへの負荷軽減
                        waitBetweenRequests();
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse product from link: {}", e.getMessage());
                }

                // 進捗ログ
                if (products.size() > 0 && products.size() % 5 == 0) {
                    logger.info("Scraped {} products so far", products.size());
                }
            }

        } catch (Exception e) {
            logger.error("Error scraping products: {}", e.getMessage(), e);
        }

        return products;
    }

    /**
     * 商品詳細ページから商品情報を取得
     */
    private Product scrapeProductDetail(String relativeUrl) {
        String fullUrl = relativeUrl.startsWith("http") ? relativeUrl : BASE_URL + relativeUrl;

        try {
            // 詳細ページに遷移
            driver.get(fullUrl);
            Thread.sleep(2000);

            // 商品名を取得（h2タグから）
            String productName = extractProductName();
            if (productName == null || productName.isEmpty()) {
                logger.warn("Product name not found for URL: {}", fullUrl);
                return null;
            }

            // 画像URLを取得
            String imageUrl = extractImageUrl();

            // 価格を取得
            Integer price = extractPriceFromDetail();

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
            product.setSourceUrl(fullUrl);
            product.setDescription(description);
            product.setIsNew(true);

            logger.debug("Parsed product: {}", productName);

            return product;

        } catch (Exception e) {
            logger.warn("Failed to scrape product detail from {}: {}", fullUrl, e.getMessage());
            return null;
        }
    }

    /**
     * 商品名を抽出（h2タグから）
     */
    private String extractProductName() {
        try {
            List<WebElement> h2Elements = findElementsSafely(By.tagName("h2"));
            for (WebElement h2 : h2Elements) {
                String text = getElementText(h2);
                if (text != null && !text.isEmpty() && !text.equals("商品情報")) {
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
                if (src != null && src.contains("/upfiles/products/") && src.contains("_b.jpg")) {
                    return src.startsWith("http") ? src : BASE_URL + src;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract image URL: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 価格を抽出（詳細ページから）
     */
    private Integer extractPriceFromDetail() {
        try {
            String pageText = driver.findElement(By.tagName("body")).getText();

            // "■価格:400円(税込)" のような形式から数値を抽出
            Pattern pattern = Pattern.compile("■価格[：:](\\d+)円");
            Matcher matcher = pattern.matcher(pageText);

            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            logger.warn("Failed to extract price: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 発売日を抽出
     */
    private LocalDate extractReleaseDate() {
        try {
            String pageText = driver.findElement(By.tagName("body")).getText();

            // "■発売時期:2026年1月" のような形式から年月を抽出
            Pattern pattern = Pattern.compile("■発売時期[：:]?(\\d{4})年(\\d{1,2})月");
            Matcher matcher = pattern.matcher(pageText);

            if (matcher.find()) {
                int year = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));

                // 月の最初の日を返す
                return LocalDate.of(year, month, 1);
            }
        } catch (Exception e) {
            logger.warn("Failed to extract release date: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 商品説明を抽出
     */
    private String extractDescription() {
        try {
            String pageText = driver.findElement(By.tagName("body")).getText();

            // 簡易的な説明を作成
            StringBuilder description = new StringBuilder("タカラトミーアーツ公式サイトより");

            // "全X種" のようなラインナップ情報を探す
            Pattern lineupPattern = Pattern.compile("全(\\d+)種");
            Matcher lineupMatcher = lineupPattern.matcher(pageText);
            if (lineupMatcher.find()) {
                description.append(" - 全").append(lineupMatcher.group(1)).append("種");
            }

            return description.toString();

        } catch (Exception e) {
            logger.warn("Failed to extract description: {}", e.getMessage());
            return "タカラトミーアーツ公式サイトより";
        }
    }
}
