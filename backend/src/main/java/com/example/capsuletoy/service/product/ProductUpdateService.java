package com.example.capsuletoy.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.capsuletoy.domain.product.DuplicateChecker;
import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductUpdateService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DuplicateChecker duplicateChecker;

    @Autowired
    ProductDeleteService productDeleteService;

    // スクレイピング結果の保存（重複チェック付き）
    @Transactional
    public Product saveScrapedProduct(Product scrapedProduct) {
        Optional<Product> existing = duplicateChecker.findExistingProduct(
            scrapedProduct.getProductName(),
            scrapedProduct.getManufacturer(),
            scrapedProduct.getSourceUrl()
        );

        if (existing.isPresent()) {
            // 既存商品の更新
            Product existingProduct = existing.get();
            productDeleteService.deleteProduct(existingProduct.getId());

        }

        // 新規商品として保存（is_newはtrueで自動設定）
        return saveProduct(scrapedProduct);
    }

    // 商品保存（新規作成または更新）
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // 商品の一括保存
    @Transactional
    public List<Product> saveAllProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }
}
