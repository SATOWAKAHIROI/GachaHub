package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品情報のREST APIコントローラー
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 商品一覧取得（ページネーション・フィルタ・ソート対応）
     * GET /api/products?page=0&size=20&sort=createdAt,desc&manufacturer=BANDAI
     */
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String keyword) {

        Sort sortOrder = direction.equalsIgnoreCase("asc")
                ? Sort.by(sort).ascending()
                : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<Product> productPage;

        if (manufacturer != null && keyword != null) {
            // メーカー + キーワード検索
            productPage = productService.searchByManufacturerAndKeyword(manufacturer, keyword, pageable);
        } else if (manufacturer != null) {
            // メーカー別フィルタ
            productPage = productService.getProductsByManufacturer(manufacturer, pageable);
        } else if (keyword != null) {
            // キーワード検索
            productPage = productService.searchProductsByName(keyword, pageable);
        } else {
            // 全商品取得
            productPage = productService.getAllProducts(pageable);
        }

        return ResponseEntity.ok(buildPageResponse(productPage));
    }

    /**
     * 商品詳細取得
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 新着商品一覧取得
     * GET /api/products/new?page=0&size=10
     */
    @GetMapping("/new")
    public ResponseEntity<?> getNewProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productService.getNewProducts(pageable);

        return ResponseEntity.ok(buildPageResponse(productPage));
    }

    /**
     * ページネーションレスポンスを構築
     */
    private Map<String, Object> buildPageResponse(Page<Product> productPage) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", productPage.getContent());
        response.put("totalElements", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("currentPage", productPage.getNumber());
        response.put("size", productPage.getSize());
        response.put("hasNext", productPage.hasNext());
        response.put("hasPrevious", productPage.hasPrevious());
        return response;
    }
}
