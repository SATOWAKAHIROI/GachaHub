package com.example.capsuletoy.service;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 全商品取得
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ページネーション対応の商品取得
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // ID指定で商品取得
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // メーカー別商品取得
    public List<Product> getProductsByManufacturer(String manufacturer) {
        return productRepository.findByManufacturer(manufacturer);
    }

    // メーカー別商品取得（ページネーション対応）
    public Page<Product> getProductsByManufacturer(String manufacturer, Pageable pageable) {
        return productRepository.findByManufacturer(manufacturer, pageable);
    }

    // 新着商品取得
    public List<Product> getNewProducts() {
        return productRepository.findByIsNewTrue();
    }

    // 新着商品取得（ページネーション対応）
    public Page<Product> getNewProducts(Pageable pageable) {
        return productRepository.findByIsNewTrue(pageable);
    }

    // 商品名検索
    public List<Product> searchProductsByName(String productName) {
        return productRepository.findByProductNameContaining(productName);
    }

    // 商品名検索（ページネーション対応）
    public Page<Product> searchProductsByName(String productName, Pageable pageable) {
        return productRepository.findByProductNameContaining(productName, pageable);
    }

    // メーカー別 + キーワード検索（ページネーション対応）
    public Page<Product> searchByManufacturerAndKeyword(String manufacturer, String keyword, Pageable pageable) {
        return productRepository.findByManufacturerAndKeyword(manufacturer, keyword, pageable);
    }

    // 商品保存（新規作成または更新）
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // 商品削除
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // 重複チェック（商品名とメーカーで判定）
    public boolean isDuplicate(String productName, String manufacturer) {
        List<Product> products = productRepository.findByProductNameContaining(productName);
        return products.stream()
                .anyMatch(p -> p.getManufacturer().equals(manufacturer));
    }

    // 商品が既に存在するかチェック（完全一致）
    public Optional<Product> findExistingProduct(String productName, String manufacturer, String sourceUrl) {
        List<Product> products = productRepository.findByProductNameContaining(productName);
        return products.stream()
                .filter(p -> p.getManufacturer().equals(manufacturer)
                          && (sourceUrl == null || sourceUrl.equals(p.getSourceUrl())))
                .findFirst();
    }

    // 新着フラグを更新
    @Transactional
    public void updateIsNewFlag(Long id, Boolean isNew) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setIsNew(isNew);
            productRepository.save(product);
        }
    }

    // 古い新着フラグをリセット（例：30日以上経過した商品）
    @Transactional
    public void resetOldNewFlags(int daysThreshold) {
        List<Product> newProducts = productRepository.findByIsNewTrue();
        LocalDate threshold = LocalDate.now().minusDays(daysThreshold);

        for (Product product : newProducts) {
            if (product.getCreatedAt().toLocalDate().isBefore(threshold)) {
                product.setIsNew(false);
                productRepository.save(product);
            }
        }
    }

    // 商品の一括保存
    @Transactional
    public List<Product> saveAllProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    // スクレイピング結果の保存（重複チェック付き）
    @Transactional
    public Product saveScrapedProduct(Product scrapedProduct) {
        Optional<Product> existing = findExistingProduct(
            scrapedProduct.getProductName(),
            scrapedProduct.getManufacturer(),
            scrapedProduct.getSourceUrl()
        );

        if (existing.isPresent()) {
            // 既存商品の更新
            Product existingProduct = existing.get();
            existingProduct.setProductName(scrapedProduct.getProductName());
            existingProduct.setImageUrl(scrapedProduct.getImageUrl());
            existingProduct.setReleaseDate(scrapedProduct.getReleaseDate());
            existingProduct.setPrice(scrapedProduct.getPrice());
            existingProduct.setDescription(scrapedProduct.getDescription());
            existingProduct.setLineupInfo(scrapedProduct.getLineupInfo());
            existingProduct.setSourceUrl(scrapedProduct.getSourceUrl());
            // is_newは既存商品なのでfalseに保つ
            existingProduct.setIsNew(false);
            return productRepository.save(existingProduct);
        } else {
            // 新規商品として保存（is_newはtrueで自動設定）
            return productRepository.save(scrapedProduct);
        }
    }
}
