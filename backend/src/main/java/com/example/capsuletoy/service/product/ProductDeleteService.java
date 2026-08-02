package com.example.capsuletoy.service.product;

import org.springframework.stereotype.Service;

import com.example.capsuletoy.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductDeleteService {
    private final ProductRepository productRepository;

    public ProductDeleteService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 商品削除
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
