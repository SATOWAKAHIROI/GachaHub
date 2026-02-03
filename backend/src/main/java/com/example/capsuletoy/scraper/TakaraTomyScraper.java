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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * タカラトミーアーツ公式サイトのスクレイパー
 * カレンダーページから発売予定商品を取得（今月と翌月）
 */
@Component
public class TakaraTomyScraper extends BaseScraper {

    private static final Logger logger = LoggerFactory.getLogger(TakaraTomyScraper.class);
    private static final String BASE_URL = "https://www.takaratomy-arts.co.jp";
    private static final String CALENDAR_BASE_URL = "https://www.takaratomy-arts.co.jp/items/gacha/calendar/";

    // 最大処理件数（詳細ページ遷移があるため制限）
    private static final int MAX_PRODUCTS = 50;

    // 重複チェック用（複数ページ間で共有）
    private Set<String> processedUrls;

    @Override
    protected String getTargetUrl() {
        // 今月のカレンダーURLを返す（テスト用）
        return buildCalendarUrl(LocalDate.now());
    }

    @Override
    protected String getManufacturerName() {
        return "TAKARA_TOMY";
    }

    /**
     * スクレイピング実行（オーバーライド）
     * 今月と翌月の2ヶ月分をスクレイピング
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

            LocalDate now = LocalDate.now();
            LocalDate nextMonth = now.plusMonths(1);

            // 今月のカレンダーをスクレイピング
            String thisMonthUrl = buildCalendarUrl(now);
            logger.info("Scraping this month's calendar: {}", thisMonthUrl);
            List<Product> thisMonthProducts = scrapeCalendarPage(thisMonthUrl);
            products.addAll(thisMonthProducts);
            logger.info("Found {} products from this month", thisMonthProducts.size());

            // 翌月のカレンダーをスクレイピング
            String nextMonthUrl = buildCalendarUrl(nextMonth);
            logger.info("Scraping next month's calendar: {}", nextMonthUrl);
            List<Product> nextMonthProducts = scrapeCalendarPage(nextMonthUrl);
            products.addAll(nextMonthProducts);
            logger.info("Found {} products from next month", nextMonthProducts.size());

            logger.info("Scraped {} products total from {}", products.size(), getManufacturerName());

        } catch (Exception e) {
            logger.error("Error during scraping for {}: {}", getManufacturerName(), e.getMessage(), e);
        } finally {
            // WebDriver終了
            scraperConfig.quitDriver(driver);
        }

        return products;
    }

    /**
     * 年月からカレンダーURLを構築
     * 例: 2026年2月 → https://www.takaratomy-arts.co.jp/items/gacha/calendar/?ym=202602
     */
    private String buildCalendarUrl(LocalDate date) {
        String ym = date.format(DateTimeFormatter.ofPattern("yyyyMM"));
        return CALENDAR_BASE_URL + "?ym=" + ym;
    }

    /**
     * カレンダーページをスクレイピング
     */
    private List<Product> scrapeCalendarPage(String calendarUrl) {
        List<Product> products = new ArrayList<>();

        try {
            // カレンダーページにアクセス
            driver.get(calendarUrl);
            waitForPageLoad();

            // 商品リンクを取得してスクレイピング
            List<WebElement> linkElements = findElementsSafely(By.tagName("a"));
            logger.info("Found {} link elements on page", linkElements.size());

            for (WebElement linkElement : linkElements) {
                // 最大件数に達したら終了
                if (products.size() >= MAX_PRODUCTS) {
                    logger.info("Reached max product limit ({}), stopping", MAX_PRODUCTS);
                    break;
                }

                try {
                    String href = getElementAttribute(linkElement, "href");

                    // item.html?n= を含むリンクのみ処理
                    if (href != null && href.contains("item.html?n=")) {
                        // 完全なURLに変換
                        String fullUrl = normalizeUrl(href);

                        // 重複チェック（複数ページ間で共有）
                        if (processedUrls.contains(fullUrl)) {
                            continue;
                        }
                        processedUrls.add(fullUrl);

                        // 詳細ページにアクセスして商品情報を取得
                        Product product = scrapeProductDetail(fullUrl);
                        if (product != null) {
                            products.add(product);

                            // 進捗ログ（10件ごと）
                            if (products.size() % 10 == 0) {
                                logger.info("Progress: {} products scraped", products.size());
                            }
                        }

                        // サイトへの負荷軽減
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse product from link: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("Error scraping calendar page {}: {}", calendarUrl, e.getMessage(), e);
        }

        return products;
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
        // scrape()をオーバーライドしているため、このメソッドは直接呼ばれない
        // 互換性のため空リストを返す
        return new ArrayList<>();
    }

    /**
     * URLを正規化（相対パスを絶対パスに変換）
     */
    private String normalizeUrl(String href) {
        if (href.startsWith("http")) {
            return href;
        }

        // ../../item.html?n=XXX のような相対パスを処理
        if (href.contains("../../item.html")) {
            // カレンダーページ（/items/gacha/calendar/）からの相対パス
            return BASE_URL + "/items/item.html" + href.substring(href.indexOf("?"));
        }

        // その他の相対パス
        if (href.startsWith("/")) {
            return BASE_URL + href;
        }

        return BASE_URL + "/" + href;
    }

    /**
     * 商品詳細ページから商品情報を取得
     */
    private Product scrapeProductDetail(String fullUrl) {
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
