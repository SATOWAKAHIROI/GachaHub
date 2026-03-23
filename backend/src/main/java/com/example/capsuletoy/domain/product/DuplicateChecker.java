package com.example.capsuletoy.domain.product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;

@Component
public class DuplicateChecker {

    @Autowired
    ProductRepository productRepository;

    // 重複チェック（商品名とメーカーで判定）
    public boolean isDuplicate(String productName, String manufacturer) {
        List<Product> products = productRepository.findByProductNameContaining(productName);
        Stream<Product> productsStream = products.stream();
        return productsStream.anyMatch(p -> checkManufacture(p, manufacturer));
    }

    // 商品が既に存在するかチェック（完全一致）
    public Optional<Product> findExistingProduct(String productName, String manufacturer, String sourceUrl) {
        List<Product> products = productRepository.findByProductNameContaining(productName);
        Stream<Product> productsStream = products.stream();
        Stream<Product> filteredProductsStream = productsStream.filter(p -> checkManufacture(p, manufacturer) && checkSourceUrlOrNull(p, sourceUrl));
        return filteredProductsStream.findFirst();
    }

    // 製造メーカー一致判定
    public boolean checkManufacture(Product product, String manufacturer){
        String targetManufacture = product.getManufacturer();
        return targetManufacture.equals(manufacturer);
    }

    // URL一致とNull判定
    public boolean checkSourceUrlOrNull(Product product, String sourceUrl){
        if (sourceUrl == null) {
            return true;
        }

        String targetSourceUrl = product.getSourceUrl();
        if (sourceUrl.equals(targetSourceUrl)){
            return true;
        }

        return false;
    }
}
