package com.example.capsuletoy.controller.scrape.status;

import com.example.capsuletoy.domain.log.ScrapeLogAdministrater;
import com.example.capsuletoy.model.ScrapeLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * スクレイピング管理用コントローラー
 * 管理者が手動でスクレイピングを実行するためのエンドポイント
 */
@RestController
@RequestMapping("/api/scrape")
public class ScrapeStatusController {
    @Autowired
    private ScrapeLogAdministrater scrapeLogAdministrater;

    /**
     * スクレイピングステータス確認
     * GET /api/scrape/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getScrapeStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("available", true);
        status.put("supportedSites", List.of("BANDAI_GASHAPON", "TAKARA_TOMY_ARTS"));

        // 最新のログを取得
        List<ScrapeLog> recentLogs = scrapeLogAdministrater.getRecentScrapeLogs(1);
        if (!recentLogs.isEmpty()) {
            ScrapeLog latestLog = recentLogs.get(0);
            status.put("lastExecution", latestLog.getExecutedAt());
            status.put("lastStatus", latestLog.getStatus());
        }

        return ResponseEntity.ok(status);
    }
}
