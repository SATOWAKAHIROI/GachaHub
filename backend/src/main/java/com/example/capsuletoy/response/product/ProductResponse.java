package com.example.capsuletoy.response.product;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.capsuletoy.model.Product;

public final class ProductResponse {
    /**
     * ページネーションレスポンスを構築
     */
    public static Map<String, Object> buildPageResponse(Page<Product> productPage) {
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
