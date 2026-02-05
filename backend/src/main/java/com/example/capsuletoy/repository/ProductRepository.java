package com.example.capsuletoy.repository;

import com.example.capsuletoy.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // メーカー別で検索
    List<Product> findByManufacturer(String manufacturer);

    // メーカー別で検索（ページネーション対応）
    Page<Product> findByManufacturer(String manufacturer, Pageable pageable);

    // 新着商品のみ取得
    List<Product> findByIsNewTrue();

    // メーカー別の新着商品を取得
    List<Product> findByIsNewTrueAndManufacturer(String manufacturer);

    // 新着商品のみ取得（ページネーション対応）
    Page<Product> findByIsNewTrue(Pageable pageable);

    // 商品名で検索（部分一致）
    List<Product> findByProductNameContaining(String productName);

    // 商品名で検索（部分一致、ページネーション対応）
    Page<Product> findByProductNameContaining(String productName, Pageable pageable);

    // メーカー別 + キーワード検索（ページネーション対応）
    @Query("SELECT p FROM Product p WHERE p.manufacturer = :manufacturer AND p.productName LIKE %:keyword%")
    Page<Product> findByManufacturerAndKeyword(@Param("manufacturer") String manufacturer, @Param("keyword") String keyword, Pageable pageable);
}
