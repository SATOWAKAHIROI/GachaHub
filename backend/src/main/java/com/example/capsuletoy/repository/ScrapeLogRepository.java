package com.example.capsuletoy.repository;

import com.example.capsuletoy.model.ScrapeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapeLogRepository extends JpaRepository<ScrapeLog, Long> {

    // 対象サイト別でログを取得
    List<ScrapeLog> findByTargetSite(String targetSite);

    // ステータス別でログを取得
    List<ScrapeLog> findByStatus(String status);

    // 実行日時の降順で全ログを取得
    List<ScrapeLog> findAllByOrderByExecutedAtDesc();
}
