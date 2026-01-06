package com.example.capsuletoy.repository;

import com.example.capsuletoy.model.ScrapeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapeConfigRepository extends JpaRepository<ScrapeConfig, Long> {

    // 有効な設定のみ取得
    List<ScrapeConfig> findByIsEnabledTrue();

    // サイト名で検索
    ScrapeConfig findBySiteName(String siteName);
}
