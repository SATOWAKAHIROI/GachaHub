package com.example.capsuletoy.service;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;
import com.example.capsuletoy.service.product.ProductService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

}
