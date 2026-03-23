package com.example.capsuletoy.domain.product;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Component
public class NewFlagsAdmin {
    @Autowired
    ProductRepository productRepository;

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
}
