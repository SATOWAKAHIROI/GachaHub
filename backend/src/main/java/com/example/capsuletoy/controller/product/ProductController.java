package com.example.capsuletoy.controller.product;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.response.product.ProductResponse;
import com.example.capsuletoy.service.product.ProductPagenationService;
import com.example.capsuletoy.service.product.ProductService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 商品情報のREST APIコントローラー
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPagenationService productPagenationService;

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

        Pageable pageable = productPagenationService.buildPageable(page, size, sort, direction);
        Page<Product> productPage = productPagenationService.getProducts(manufacturer, keyword, pageable);
        return ResponseEntity.ok(ProductResponse.buildPageResponse(productPage));
    }

    /**
     * 商品詳細取得
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);

        if(product.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product.get());
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
        Page<Product> productPage = productPagenationService.getNewProducts(pageable);

        return ResponseEntity.ok(ProductResponse.buildPageResponse(productPage));
    }
}
