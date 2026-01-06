package com.example.capsuletoy.repository;

import com.example.capsuletoy.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // メーカー別で検索
    List<Product> findByManufacturer(String manufacturer);

    // 新着商品のみ取得
    List<Product> findByIsNewTrue();

    // 商品名で検索（部分一致）
    List<Product> findByProductNameContaining(String productName);
}
