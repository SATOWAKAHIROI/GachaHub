package com.example.capsuletoy.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@Configuration
public class ScraperConfig {

    @Value("${selenium.remote-url:}")
    private String seleniumRemoteUrl;

    /**
     * Chrome WebDriverを作成
     * selenium.remote-urlが設定されている場合はRemoteWebDriver（Docker環境）
     * 未設定の場合はローカルChromeDriver（開発環境）
     */
    public WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();

        // ヘッドレスモード有効化（サーバー環境用）
        options.addArguments("--headless=new");

        // Docker/Linux環境用の追加オプション
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        // ウィンドウサイズ指定
        options.addArguments("--window-size=1920,1080");

        // User-Agent設定（ボット検知回避）
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // 画像読み込み無効化（パフォーマンス向上）
        options.addArguments("--blink-settings=imagesEnabled=false");

        WebDriver driver;

        if (seleniumRemoteUrl != null && !seleniumRemoteUrl.isEmpty()) {
            // Docker環境: Seleniumコンテナに接続
            try {
                driver = new RemoteWebDriver(new URL(seleniumRemoteUrl), options);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid Selenium remote URL: " + seleniumRemoteUrl, e);
            }
        } else {
            // ローカル開発環境: WebDriverManagerで自動セットアップ
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(options);
        }

        // タイムアウト設定
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        return driver;
    }

    /**
     * WebDriverを安全に終了
     */
    public void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error while quitting WebDriver: " + e.getMessage());
            }
        }
    }
}
