package com.example.capsuletoy.service;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ProductServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Product createTestProduct(Long id, String name, String manufacturer, boolean isNew) {
        Product product = new Product();
        product.setId(id);
        product.setProductName(name);
        product.setManufacturer(manufacturer);
        product.setIsNew(isNew);
        product.setPrice(300);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    @Test
    void getAllProducts_ページネーション対応() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(createTestProduct(1L, "商品A", "BANDAI", true));
        Page<Product> page = new PageImpl<>(products, pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.getAllProducts(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("商品A", result.getContent().get(0).getProductName());
    }

    @Test
    void getProductById_存在する場合() {
        Product product = createTestProduct(1L, "テスト商品", "BANDAI", true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("テスト商品", result.get().getProductName());
    }

    @Test
    void getProductById_存在しない場合() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void isDuplicate_重複あり() {
        Product existing = createTestProduct(1L, "テスト商品", "BANDAI", false);
        when(productRepository.findByProductNameContaining("テスト商品")).thenReturn(List.of(existing));

        boolean result = productService.isDuplicate("テスト商品", "BANDAI");

        assertTrue(result);
    }

    @Test
    void isDuplicate_同名だがメーカー違い() {
        Product existing = createTestProduct(1L, "テスト商品", "TAKARA_TOMY", false);
        when(productRepository.findByProductNameContaining("テスト商品")).thenReturn(List.of(existing));

        boolean result = productService.isDuplicate("テスト商品", "BANDAI");

        assertFalse(result);
    }

    @Test
    void isDuplicate_重複なし() {
        when(productRepository.findByProductNameContaining("新商品")).thenReturn(Collections.emptyList());

        boolean result = productService.isDuplicate("新商品", "BANDAI");

        assertFalse(result);
    }

    @Test
    void saveScrapedProduct_新規商品の場合() {
        Product scraped = createTestProduct(null, "新商品", "BANDAI", true);
        Product saved = createTestProduct(1L, "新商品", "BANDAI", true);

        when(productRepository.findByProductNameContaining("新商品")).thenReturn(Collections.emptyList());
        when(productRepository.save(scraped)).thenReturn(saved);

        Product result = productService.saveScrapedProduct(scraped);

        assertEquals(1L, result.getId());
        assertTrue(result.getIsNew());
        verify(productRepository).save(scraped);
    }

    @Test
    void saveScrapedProduct_既存商品の場合は更新してisNewをfalseにする() {
        Product existing = createTestProduct(1L, "既存商品", "BANDAI", true);
        existing.setSourceUrl("https://example.com/1");

        Product scraped = new Product();
        scraped.setProductName("既存商品");
        scraped.setManufacturer("BANDAI");
        scraped.setSourceUrl("https://example.com/1");
        scraped.setPrice(500);
        scraped.setImageUrl("https://example.com/new-img.jpg");

        when(productRepository.findByProductNameContaining("既存商品")).thenReturn(List.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.saveScrapedProduct(scraped);

        assertFalse(result.getIsNew());
        assertEquals(500, result.getPrice());
        assertEquals("https://example.com/new-img.jpg", result.getImageUrl());
    }

    @Test
    void findExistingProduct_完全一致で見つかる() {
        Product existing = createTestProduct(1L, "テスト商品", "BANDAI", false);
        existing.setSourceUrl("https://example.com/1");

        when(productRepository.findByProductNameContaining("テスト商品")).thenReturn(List.of(existing));

        Optional<Product> result = productService.findExistingProduct("テスト商品", "BANDAI", "https://example.com/1");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findExistingProduct_sourceUrlがnullの場合はメーカーのみで判定() {
        Product existing = createTestProduct(1L, "テスト商品", "BANDAI", false);
        existing.setSourceUrl("https://example.com/1");

        when(productRepository.findByProductNameContaining("テスト商品")).thenReturn(List.of(existing));

        Optional<Product> result = productService.findExistingProduct("テスト商品", "BANDAI", null);

        assertTrue(result.isPresent());
    }

    @Test
    void updateIsNewFlag_正常に更新() {
        Product product = createTestProduct(1L, "テスト商品", "BANDAI", true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productService.updateIsNewFlag(1L, false);

        assertFalse(product.getIsNew());
        verify(productRepository).save(product);
    }

    @Test
    void updateIsNewFlag_存在しないIDでは何もしない() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        productService.updateIsNewFlag(999L, false);

        verify(productRepository, never()).save(any());
    }

    @Test
    void resetOldNewFlags_閾値を超えた商品のフラグをリセット() {
        Product oldProduct = createTestProduct(1L, "古い商品", "BANDAI", true);
        oldProduct.setCreatedAt(LocalDateTime.now().minusDays(31));

        Product newProduct = createTestProduct(2L, "新しい商品", "BANDAI", true);
        newProduct.setCreatedAt(LocalDateTime.now().minusDays(5));

        when(productRepository.findByIsNewTrue()).thenReturn(Arrays.asList(oldProduct, newProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productService.resetOldNewFlags(30);

        assertFalse(oldProduct.getIsNew());
        assertTrue(newProduct.getIsNew());
        verify(productRepository, times(1)).save(oldProduct);
        verify(productRepository, never()).save(newProduct);
    }

    @Test
    void getProductsByManufacturer_ページネーション対応() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(createTestProduct(1L, "バンダイ商品", "BANDAI", true));
        Page<Product> page = new PageImpl<>(products, pageable, 1);

        when(productRepository.findByManufacturer("BANDAI", pageable)).thenReturn(page);

        Page<Product> result = productService.getProductsByManufacturer("BANDAI", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("BANDAI", result.getContent().get(0).getManufacturer());
    }

    @Test
    void searchProductsByName_ページネーション対応() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(createTestProduct(1L, "ガチャガチャ", "BANDAI", true));
        Page<Product> page = new PageImpl<>(products, pageable, 1);

        when(productRepository.findByProductNameContaining("ガチャ", pageable)).thenReturn(page);

        Page<Product> result = productService.searchProductsByName("ガチャ", pageable);

        assertEquals(1, result.getTotalElements());
    }
}
