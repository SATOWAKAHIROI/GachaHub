package com.example.capsuletoy.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.capsuletoy.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductDeleteService {
    @Autowired
    ProductRepository productRepository;

    // 商品削除
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
