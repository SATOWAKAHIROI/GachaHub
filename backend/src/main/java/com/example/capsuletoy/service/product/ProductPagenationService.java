package com.example.capsuletoy.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;

@Service
public class ProductPagenationService {
    @Autowired
    ProductRepository productRepository;

    // ページネーション対応の商品取得
    private Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // メーカー別商品取得（ページネーション対応）
    private Page<Product> getProductsByManufacturer(String manufacturer, Pageable pageable) {
        return productRepository.findByManufacturer(manufacturer, pageable);
    }

    // 新着商品取得（ページネーション対応）
    public Page<Product> getNewProducts(Pageable pageable) {
        return productRepository.findByIsNewTrue(pageable);
    }

    // 商品名検索（ページネーション対応）
    private Page<Product> searchProductsByName(String productName, Pageable pageable) {
        return productRepository.findByProductNameContaining(productName, pageable);
    }

    // メーカー別 + キーワード検索（ページネーション対応）
    private Page<Product> searchByManufacturerAndKeyword(String manufacturer, String keyword, Pageable pageable) {
        return productRepository.findByManufacturerAndKeyword(manufacturer, keyword, pageable);
    }

    public Pageable buildPageable(int page, int size, String sort, String direction){
        Sort sortOrder;

        if(direction.equalsIgnoreCase("asc")){
            sortOrder = Sort.by(sort).ascending();
        }else{
            sortOrder = Sort.by(sort).descending();
        }
        
        return PageRequest.of(page, size, sortOrder);
    }

    public Page<Product> getProducts(String manufacturer, String keyword, Pageable pageable){
        Page<Product> productPage;

        if (manufacturer != null && keyword != null) {
            // メーカー + キーワード検索
            productPage = searchByManufacturerAndKeyword(manufacturer, keyword, pageable);
        } else if (manufacturer != null) {
            // メーカー別フィルタ
            productPage = getProductsByManufacturer(manufacturer, pageable);
        } else if (keyword != null) {
            // キーワード検索
            productPage = searchProductsByName(keyword, pageable);
        } else {
            // 全商品取得
            productPage = getAllProducts(pageable);
        }

        return productPage;
    }
}
