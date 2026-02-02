package com.example.capsuletoy.scraper;

import com.example.capsuletoy.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * TakaraToMyScraperの単体テスト（モック使用）
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class TakaraToMyScraperTest {

    @InjectMocks
    private TakaraTomyScraper takaraTomyScraper;

    @Mock
    private ScraperConfig scraperConfig;

    @Mock
    private WebDriver driver;

    @Mock
    private WebDriver.Options options;

    @Mock
    private WebDriver.Timeouts timeouts;

    @BeforeEach
    void setUp() {
        when(scraperConfig.createChromeDriver()).thenReturn(driver);
        when(driver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);
    }

    @Test
    void getTargetUrl_タカラトミーURLを返す() {
        assertEquals("https://www.takaratomy-arts.co.jp/items/gacha.html", takaraTomyScraper.getTargetUrl());
    }

    @Test
    void getManufacturerName_TAKARA_TOMYを返す() {
        assertEquals("TAKARA_TOMY", takaraTomyScraper.getManufacturerName());
    }

    @Test
    void scrape_詳細ページから商品情報を正常にパース() {
        // 一覧ページのリンク要素
        WebElement linkElement = mock(WebElement.class);
        when(linkElement.getAttribute("href")).thenReturn("/items/item.html?n=12345");

        // 非商品リンク
        WebElement otherLink = mock(WebElement.class);
        when(otherLink.getAttribute("href")).thenReturn("/items/gacha.html");

        // 一覧ページのリンク一覧を返す
        when(driver.findElements(By.tagName("a")))
                .thenReturn(Arrays.asList(otherLink, linkElement)) // 一覧ページ
                .thenReturn(Collections.emptyList()); // 詳細ページのa要素

        // 詳細ページのh2要素（商品名）
        WebElement h2Element = mock(WebElement.class);
        when(h2Element.getText()).thenReturn("テストガチャ商品");
        when(driver.findElements(By.tagName("h2"))).thenReturn(List.of(h2Element));

        // 詳細ページの画像要素
        WebElement imgElement = mock(WebElement.class);
        when(imgElement.getAttribute("src")).thenReturn("/upfiles/products/test_b.jpg");
        when(driver.findElements(By.tagName("img"))).thenReturn(List.of(imgElement));

        // 詳細ページのbodyテキスト
        WebElement bodyElement = mock(WebElement.class);
        when(bodyElement.getText()).thenReturn(
                "商品情報\n■価格:400円(税込)\n■発売時期:2026年3月\n全5種\nテスト説明");
        when(driver.findElement(By.tagName("body"))).thenReturn(bodyElement);

        List<Product> products = takaraTomyScraper.scrape();

        assertEquals(1, products.size());
        Product product = products.get(0);
        assertEquals("テストガチャ商品", product.getProductName());
        assertEquals("TAKARA_TOMY", product.getManufacturer());
        assertEquals(400, product.getPrice());
        assertEquals(LocalDate.of(2026, 3, 1), product.getReleaseDate());
        assertEquals("https://www.takaratomy-arts.co.jp/upfiles/products/test_b.jpg", product.getImageUrl());
        assertTrue(product.getDescription().contains("全5種"));
        assertTrue(product.getIsNew());

        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_リンクが空の場合は空リストを返す() {
        when(driver.findElements(By.tagName("a"))).thenReturn(Collections.emptyList());

        List<Product> products = takaraTomyScraper.scrape();

        assertEquals(0, products.size());
        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_重複URLはスキップされる() {
        WebElement link1 = mock(WebElement.class);
        WebElement link2 = mock(WebElement.class);
        when(link1.getAttribute("href")).thenReturn("/items/item.html?n=100");
        when(link2.getAttribute("href")).thenReturn("/items/item.html?n=100"); // 同じURL

        when(driver.findElements(By.tagName("a"))).thenReturn(Arrays.asList(link1, link2));

        // 詳細ページモック
        WebElement h2 = mock(WebElement.class);
        when(h2.getText()).thenReturn("重複テスト商品");
        when(driver.findElements(By.tagName("h2"))).thenReturn(List.of(h2));
        when(driver.findElements(By.tagName("img"))).thenReturn(Collections.emptyList());

        WebElement body = mock(WebElement.class);
        when(body.getText()).thenReturn("テスト本文");
        when(driver.findElement(By.tagName("body"))).thenReturn(body);

        List<Product> products = takaraTomyScraper.scrape();

        // 重複URLは1回だけ処理される
        assertEquals(1, products.size());
    }

    @Test
    void scrape_商品名がない場合はスキップ() {
        WebElement linkElement = mock(WebElement.class);
        when(linkElement.getAttribute("href")).thenReturn("/items/item.html?n=999");

        when(driver.findElements(By.tagName("a"))).thenReturn(List.of(linkElement));

        // h2が空
        when(driver.findElements(By.tagName("h2"))).thenReturn(Collections.emptyList());

        List<Product> products = takaraTomyScraper.scrape();

        assertEquals(0, products.size());
        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_WebDriver例外時は空リストを返しドライバーは終了される() {
        doThrow(new RuntimeException("Connection error")).when(driver).get(anyString());

        List<Product> products = takaraTomyScraper.scrape();

        assertEquals(0, products.size());
        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_価格なし発売日なしでも商品は作成される() {
        WebElement linkElement = mock(WebElement.class);
        when(linkElement.getAttribute("href")).thenReturn("/items/item.html?n=555");

        when(driver.findElements(By.tagName("a"))).thenReturn(List.of(linkElement));

        WebElement h2 = mock(WebElement.class);
        when(h2.getText()).thenReturn("価格未定商品");
        when(driver.findElements(By.tagName("h2"))).thenReturn(List.of(h2));
        when(driver.findElements(By.tagName("img"))).thenReturn(Collections.emptyList());

        WebElement body = mock(WebElement.class);
        when(body.getText()).thenReturn("詳細情報なし");
        when(driver.findElement(By.tagName("body"))).thenReturn(body);

        List<Product> products = takaraTomyScraper.scrape();

        assertEquals(1, products.size());
        assertNull(products.get(0).getPrice());
        assertNull(products.get(0).getReleaseDate());
    }
}
