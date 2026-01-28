package com.example.capsuletoy.scraper;

import com.example.capsuletoy.model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * スクレイピング処理の基底クラス
 * 各サイト固有のスクレイパーはこのクラスを継承して実装
 */
public abstract class BaseScraper {

    private static final Logger logger = LoggerFactory.getLogger(BaseScraper.class);

    @Autowired
    protected ScraperConfig scraperConfig;

    protected WebDriver driver;
    protected WebDriverWait wait;

    /**
     * スクレイピング実行
     */
    public List<Product> scrape() {
        List<Product> products = new ArrayList<>();

        try {
            // WebDriver初期化
            driver = scraperConfig.createChromeDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            logger.info("Starting scraping for: {}", getManufacturerName());

            // サイトにアクセス
            driver.get(getTargetUrl());

            // ページ読み込み待機
            waitForPageLoad();

            // 商品情報取得（サブクラスで実装）
            products = scrapeProducts();

            logger.info("Scraped {} products from {}", products.size(), getManufacturerName());

        } catch (Exception e) {
            logger.error("Error during scraping for {}: {}", getManufacturerName(), e.getMessage(), e);
        } finally {
            // WebDriver終了
            scraperConfig.quitDriver(driver);
        }

        return products;
    }

    /**
     * 対象サイトのURL取得（サブクラスで実装）
     */
    protected abstract String getTargetUrl();

    /**
     * メーカー名取得（サブクラスで実装）
     */
    protected abstract String getManufacturerName();

    /**
     * 商品情報のスクレイピング（サブクラスで実装）
     */
    protected abstract List<Product> scrapeProducts();

    /**
     * ページ読み込み完了を待機（サブクラスでオーバーライド可能）
     */
    protected void waitForPageLoad() {
        try {
            Thread.sleep(2000); // デフォルトで2秒待機
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 要素のテキストを安全に取得
     */
    protected String getElementText(WebElement element) {
        try {
            return element != null ? element.getText().trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 要素の属性値を安全に取得
     */
    protected String getElementAttribute(WebElement element, String attribute) {
        try {
            return element != null ? element.getAttribute(attribute) : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * セレクタで要素を安全に検索
     */
    protected WebElement findElementSafely(By by) {
        try {
            return driver.findElement(by);
        } catch (Exception e) {
            logger.warn("Element not found: {}", by);
            return null;
        }
    }

    /**
     * セレクタで要素リストを安全に検索
     */
    protected List<WebElement> findElementsSafely(By by) {
        try {
            return driver.findElements(by);
        } catch (Exception e) {
            logger.warn("Elements not found: {}", by);
            return new ArrayList<>();
        }
    }

    /**
     * 要素が表示されるまで待機
     */
    protected void waitForElement(By by) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.warn("Timeout waiting for element: {}", by);
        }
    }

    /**
     * スクレイピング間隔の待機（サイトへの負荷軽減）
     */
    protected void waitBetweenRequests() {
        try {
            Thread.sleep(1000 + (long) (Math.random() * 2000)); // 1～3秒ランダム待機
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
