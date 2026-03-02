package com.example.capsuletoy.service.product;

import com.example.capsuletoy.repository.ProductRepository;

import jakarta.transaction.Transactional;

public class ProductDeleteService {
    ProductRepository productRepository;

    // 商品削除
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
