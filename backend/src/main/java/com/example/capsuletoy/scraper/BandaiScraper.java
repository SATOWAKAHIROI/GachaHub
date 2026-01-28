package com.example.capsuletoy.scraper;

import com.example.capsuletoy.model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * バンダイガシャポン公式サイトのスクレイパー
 */
@Component
public class BandaiScraper extends BaseScraper {

    private static final Logger logger = LoggerFactory.getLogger(BandaiScraper.class);
    private static final String BASE_URL = "https://gashapon.jp";
    private static final String TARGET_URL = "https://gashapon.jp/products/";

    @Override
    protected String getTargetUrl() {
        return TARGET_URL;
    }

    @Override
    protected String getManufacturerName() {
        return "BANDAI";
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

        try {
            // 全てのリンク要素を取得
            List<WebElement> linkElements = findElementsSafely(By.tagName("a"));

            logger.info("Found {} link elements", linkElements.size());

            for (WebElement linkElement : linkElements) {
                try {
                    String href = getElementAttribute(linkElement, "href");

                    // detail.php?jan_codeを含むリンクのみ処理
                    if (href != null && href.contains("detail.php?jan_code=")) {
                        Product product = parseProductFromLink(linkElement, href);
                        if (product != null) {
                            products.add(product);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse product from link: {}", e.getMessage());
                }

                // サイトへの負荷軽減
                if (products.size() > 0 && products.size() % 10 == 0) {
                    waitBetweenRequests();
                }
            }

        } catch (Exception e) {
            logger.error("Error scraping products: {}", e.getMessage(), e);
        }

        return products;
    }

    /**
     * リンク要素から商品情報を抽出
     */
    private Product parseProductFromLink(WebElement linkElement, String href) {
        try {
            // 画像要素を取得
            WebElement imgElement = findElementSafely(linkElement, By.tagName("img"));
            if (imgElement == null) {
                return null;
            }

            String imageUrl = getElementAttribute(imgElement, "src");

            // リンク要素内のテキストを取得
            String fullText = getElementText(linkElement);
            if (fullText == null || fullText.isEmpty()) {
                return null;
            }

            // 商品名と価格を分離
            String productName = extractProductName(fullText);
            Integer price = extractPrice(fullText);

            if (productName == null || productName.isEmpty()) {
                return null;
            }

            // 完全なURLを構築
            String sourceUrl = href.startsWith("http") ? href : BASE_URL + "/" + href;

            // Productオブジェクトを作成
            Product product = new Product();
            product.setProductName(productName);
            product.setManufacturer(getManufacturerName());
            product.setImageUrl(imageUrl);
            product.setPrice(price);
            product.setSourceUrl(sourceUrl);
            product.setIsNew(true);

            // 発売日は一覧ページにないためnull
            product.setReleaseDate(null);

            // 簡易的な説明
            product.setDescription("バンダイガシャポン公式サイトより");

            logger.debug("Parsed product: {}", productName);

            return product;

        } catch (Exception e) {
            logger.warn("Failed to parse product: {}", e.getMessage());
            return null;
        }
    }

    /**
     * WebElement内の子要素を安全に検索
     */
    private WebElement findElementSafely(WebElement parent, By by) {
        try {
            return parent.findElement(by);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * テキストから商品名を抽出
     * （価格やタイプ情報を除去）
     */
    private String extractProductName(String fullText) {
        if (fullText == null) {
            return null;
        }

        // 改行で分割
        String[] lines = fullText.split("\\n");

        // 最初の非空行を商品名とする
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()
                && !trimmed.contains("円")
                && !trimmed.equals("ガシャポン")
                && !trimmed.equals("カプセルトイ")) {
                return trimmed;
            }
        }

        return fullText.trim();
    }

    /**
     * テキストから価格を抽出
     */
    private Integer extractPrice(String text) {
        if (text == null) {
            return null;
        }

        // "300円" のような形式から数値を抽出
        Pattern pattern = Pattern.compile("(\\d+)円");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse price: {}", matcher.group(1));
            }
        }

        return null;
    }
}
