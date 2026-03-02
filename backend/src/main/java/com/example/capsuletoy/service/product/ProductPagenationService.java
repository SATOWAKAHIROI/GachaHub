package com.example.capsuletoy.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;

public class ProductPagenationService {
    @Autowired
    ProductRepository productRepository;

    // ページネーション対応の商品取得
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // メーカー別商品取得（ページネーション対応）
    public Page<Product> getProductsByManufacturer(String manufacturer, Pageable pageable) {
        return productRepository.findByManufacturer(manufacturer, pageable);
    }

    // 新着商品取得（ページネーション対応）
    public Page<Product> getNewProducts(Pageable pageable) {
        return productRepository.findByIsNewTrue(pageable);
    }

    // 商品名検索（ページネーション対応）
    public Page<Product> searchProductsByName(String productName, Pageable pageable) {
        return productRepository.findByProductNameContaining(productName, pageable);
    }

    // メーカー別 + キーワード検索（ページネーション対応）
    public Page<Product> searchByManufacturerAndKeyword(String manufacturer, String keyword, Pageable pageable) {
        return productRepository.findByManufacturerAndKeyword(manufacturer, keyword, pageable);
    }
}
