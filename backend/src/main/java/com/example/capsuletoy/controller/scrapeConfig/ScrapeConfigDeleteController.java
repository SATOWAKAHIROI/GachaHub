package com.example.capsuletoy.controller.scrapeConfig;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capsuletoy.response.ErrorResponse;
import com.example.capsuletoy.service.scrapeConfig.ScrapeConfigDeleteService;

@RestController
@RequestMapping("/api/scrape/configs")
public class ScrapeConfigDeleteController {
    @Autowired
    private ScrapeConfigDeleteService scrapeConfigDeleteService;

    /**
     * 設定を削除
     * DELETE /api/scrape/configs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long id) {
        try {
            scrapeConfigDeleteService.deleteConfig(id);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "設定を削除しました: ID=" + id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.errorResponse(e.getMessage()));
        }
    }
}
