package com.example.capsuletoy.service.product;

import com.example.capsuletoy.domain.product.DuplicateChecker;
import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    DuplicateChecker duplicateChecker;

    // 全商品取得
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ID指定で商品取得
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // メーカー別商品取得
    public List<Product> getProductsByManufacturer(String manufacturer) {
        return productRepository.findByManufacturer(manufacturer);
    }

    // 新着商品取得
    public List<Product> getNewProducts() {
        return productRepository.findByIsNewTrue();
    }

    // 商品名検索
    public List<Product> searchProductsByName(String productName) {
        return productRepository.findByProductNameContaining(productName);
    }
}
