package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.service.product.ProductPagenationService;
import com.example.capsuletoy.service.product.ProductService;

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

    @MockitoBean
    private ProductPagenationService productPagenationService;

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

        when(productPagenationService.getNewProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].productName").value("新着商品"))
                .andExpect(jsonPath("$.content[0].isNew").value(true));
    }
}
