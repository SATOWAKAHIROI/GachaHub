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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BandaiScraperの単体テスト（モック使用）
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class BandaiScraperTest {

    @InjectMocks
    private BandaiScraper bandaiScraper;

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
    void getTargetUrl_バンダイURLを返す() {
        assertEquals("https://gashapon.jp/products/", bandaiScraper.getTargetUrl());
    }

    @Test
    void getManufacturerName_BANDAIを返す() {
        assertEquals("BANDAI", bandaiScraper.getManufacturerName());
    }

    @Test
    void scrape_商品リンクから正常にパース() {
        // 商品リンク要素のモック
        WebElement linkElement = mock(WebElement.class);
        WebElement imgElement = mock(WebElement.class);

        when(linkElement.getAttribute("href")).thenReturn("https://gashapon.jp/detail.php?jan_code=12345");
        when(linkElement.findElement(By.tagName("img"))).thenReturn(imgElement);
        when(imgElement.getAttribute("src")).thenReturn("https://gashapon.jp/images/product.jpg");
        when(linkElement.getText()).thenReturn("テスト商品名\n300円");

        // 非商品リンク（detail.php?jan_code=を含まない）
        WebElement nonProductLink = mock(WebElement.class);
        when(nonProductLink.getAttribute("href")).thenReturn("https://gashapon.jp/about/");

        when(driver.findElements(By.tagName("a"))).thenReturn(Arrays.asList(nonProductLink, linkElement));

        List<Product> products = bandaiScraper.scrape();

        assertEquals(1, products.size());
        Product product = products.get(0);
        assertEquals("テスト商品名", product.getProductName());
        assertEquals("BANDAI", product.getManufacturer());
        assertEquals(300, product.getPrice());
        assertEquals("https://gashapon.jp/images/product.jpg", product.getImageUrl());
        assertTrue(product.getIsNew());

        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_画像なしリンクはスキップ() {
        WebElement linkElement = mock(WebElement.class);
        when(linkElement.getAttribute("href")).thenReturn("https://gashapon.jp/detail.php?jan_code=12345");
        when(linkElement.findElement(By.tagName("img"))).thenReturn(null);

        when(driver.findElements(By.tagName("a"))).thenReturn(List.of(linkElement));

        List<Product> products = bandaiScraper.scrape();

        assertEquals(0, products.size());
        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_テキストなしリンクはスキップ() {
        WebElement linkElement = mock(WebElement.class);
        WebElement imgElement = mock(WebElement.class);

        when(linkElement.getAttribute("href")).thenReturn("https://gashapon.jp/detail.php?jan_code=12345");
        when(linkElement.findElement(By.tagName("img"))).thenReturn(imgElement);
        when(imgElement.getAttribute("src")).thenReturn("https://gashapon.jp/images/product.jpg");
        when(linkElement.getText()).thenReturn("");

        when(driver.findElements(By.tagName("a"))).thenReturn(List.of(linkElement));

        List<Product> products = bandaiScraper.scrape();

        assertEquals(0, products.size());
        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_リンクが空の場合は空リストを返す() {
        when(driver.findElements(By.tagName("a"))).thenReturn(Collections.emptyList());

        List<Product> products = bandaiScraper.scrape();

        assertEquals(0, products.size());
        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_価格が含まれない場合はnull() {
        WebElement linkElement = mock(WebElement.class);
        WebElement imgElement = mock(WebElement.class);

        when(linkElement.getAttribute("href")).thenReturn("https://gashapon.jp/detail.php?jan_code=12345");
        when(linkElement.findElement(By.tagName("img"))).thenReturn(imgElement);
        when(imgElement.getAttribute("src")).thenReturn("https://gashapon.jp/images/product.jpg");
        when(linkElement.getText()).thenReturn("テスト商品名\n価格未定");

        when(driver.findElements(By.tagName("a"))).thenReturn(List.of(linkElement));

        List<Product> products = bandaiScraper.scrape();

        assertEquals(1, products.size());
        assertNull(products.get(0).getPrice());
    }

    @Test
    void scrape_相対URLが完全URLに変換される() {
        WebElement linkElement = mock(WebElement.class);
        WebElement imgElement = mock(WebElement.class);

        when(linkElement.getAttribute("href")).thenReturn("detail.php?jan_code=99999");
        when(linkElement.findElement(By.tagName("img"))).thenReturn(imgElement);
        when(imgElement.getAttribute("src")).thenReturn("/images/test.jpg");
        when(linkElement.getText()).thenReturn("相対URL商品");

        when(driver.findElements(By.tagName("a"))).thenReturn(List.of(linkElement));

        List<Product> products = bandaiScraper.scrape();

        assertEquals(1, products.size());
        assertEquals("https://gashapon.jp/detail.php?jan_code=99999", products.get(0).getSourceUrl());
    }

    @Test
    void scrape_WebDriver例外時は空リストを返しドライバーは終了される() {
        when(scraperConfig.createChromeDriver()).thenReturn(driver);
        doThrow(new RuntimeException("WebDriver error")).when(driver).get(anyString());

        List<Product> products = bandaiScraper.scrape();

        assertEquals(0, products.size());
        verify(scraperConfig).quitDriver(driver);
    }

    @Test
    void scrape_複数商品を正常にパース() {
        WebElement link1 = mock(WebElement.class);
        WebElement img1 = mock(WebElement.class);
        WebElement link2 = mock(WebElement.class);
        WebElement img2 = mock(WebElement.class);

        when(link1.getAttribute("href")).thenReturn("https://gashapon.jp/detail.php?jan_code=001");
        when(link1.findElement(By.tagName("img"))).thenReturn(img1);
        when(img1.getAttribute("src")).thenReturn("https://gashapon.jp/img1.jpg");
        when(link1.getText()).thenReturn("商品A\n200円");

        when(link2.getAttribute("href")).thenReturn("https://gashapon.jp/detail.php?jan_code=002");
        when(link2.findElement(By.tagName("img"))).thenReturn(img2);
        when(img2.getAttribute("src")).thenReturn("https://gashapon.jp/img2.jpg");
        when(link2.getText()).thenReturn("商品B\n500円");

        when(driver.findElements(By.tagName("a"))).thenReturn(Arrays.asList(link1, link2));

        List<Product> products = bandaiScraper.scrape();

        assertEquals(2, products.size());
        assertEquals("商品A", products.get(0).getProductName());
        assertEquals(200, products.get(0).getPrice());
        assertEquals("商品B", products.get(1).getProductName());
        assertEquals(500, products.get(1).getPrice());
    }
}
