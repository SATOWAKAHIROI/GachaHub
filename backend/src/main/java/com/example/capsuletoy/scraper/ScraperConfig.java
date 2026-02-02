package com.example.capsuletoy.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ScraperConfig {

    /**
     * Chrome WebDriverを作成
     * ヘッドレスモードで実行（UIなし）
     */
    public WebDriver createChromeDriver() {
        // 環境変数でChromeDriverパスが指定されていればそれを使用（Docker環境）
        String chromeDriverBin = System.getenv("CHROMEDRIVER_BIN");
        if (chromeDriverBin != null && !chromeDriverBin.isEmpty()) {
            System.setProperty("webdriver.chrome.driver", chromeDriverBin);
        } else {
            // ローカル開発環境ではWebDriverManagerで自動セットアップ
            WebDriverManager.chromedriver().setup();
        }

        ChromeOptions options = new ChromeOptions();

        // 環境変数でChromeバイナリパスが指定されていればそれを使用（Docker環境）
        String chromeBin = System.getenv("CHROME_BIN");
        if (chromeBin != null && !chromeBin.isEmpty()) {
            options.setBinary(chromeBin);
        }

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

        // ChromeDriverインスタンス作成
        WebDriver driver = new ChromeDriver(options);

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
