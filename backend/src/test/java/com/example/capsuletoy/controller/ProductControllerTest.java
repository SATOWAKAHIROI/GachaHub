package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductControllerのエンドポイントテスト
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private Product createTestProduct(Long id, String name, String manufacturer) {
        Product product = new Product();
        product.setId(id);
        product.setProductName(name);
        product.setManufacturer(manufacturer);
        product.setPrice(300);
        product.setIsNew(true);
        product.setReleaseDate(LocalDate.of(2026, 1, 15));
        product.setImageUrl("https://example.com/img.jpg");
        product.setSourceUrl("https://example.com/product/" + id);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    @Test
    void getProducts_全商品取得() throws Exception {
        List<Product> products = List.of(
                createTestProduct(1L, "商品A", "BANDAI"),
                createTestProduct(2L, "商品B", "TAKARA_TOMY")
        );
        Page<Product> page = new PageImpl<>(products);

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].productName").value("商品A"))
                .andExpect(jsonPath("$.content[1].productName").value("商品B"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getProducts_メーカーフィルタ() throws Exception {
        List<Product> products = List.of(createTestProduct(1L, "バンダイ商品", "BANDAI"));
        Page<Product> page = new PageImpl<>(products);

        when(productService.getProductsByManufacturer(eq("BANDAI"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products").param("manufacturer", "BANDAI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].manufacturer").value("BANDAI"));
    }

    @Test
    void getProducts_キーワード検索() throws Exception {
        List<Product> products = List.of(createTestProduct(1L, "ガチャガチャ", "BANDAI"));
        Page<Product> page = new PageImpl<>(products);

        when(productService.searchProductsByName(eq("ガチャ"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products").param("keyword", "ガチャ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].productName").value("ガチャガチャ"));
    }

    @Test
    void getProducts_メーカーとキーワード組み合わせ() throws Exception {
        List<Product> products = List.of(createTestProduct(1L, "バンダイガチャ", "BANDAI"));
        Page<Product> page = new PageImpl<>(products);

        when(productService.searchByManufacturerAndKeyword(eq("BANDAI"), eq("ガチャ"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("manufacturer", "BANDAI")
                        .param("keyword", "ガチャ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getProducts_ページネーションパラメータ() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(), Pageable.ofSize(5), 0);

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    void getProduct_存在する商品() throws Exception {
        Product product = createTestProduct(1L, "テスト商品", "BANDAI");
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("テスト商品"))
                .andExpect(jsonPath("$.manufacturer").value("BANDAI"))
                .andExpect(jsonPath("$.price").value(300));
    }

    @Test
    void getProduct_存在しない商品は404() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNewProducts_新着商品取得() throws Exception {
        List<Product> products = List.of(
                createTestProduct(1L, "新着商品", "BANDAI")
        );
        Page<Product> page = new PageImpl<>(products);

        when(productService.getNewProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].productName").value("新着商品"))
                .andExpect(jsonPath("$.content[0].isNew").value(true));
    }
}
