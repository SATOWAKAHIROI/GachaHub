package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.service.ScrapeConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ScrapeConfigControllerのエンドポイントテスト
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class ScrapeConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ScrapeConfigService scrapeConfigService;

    private ScrapeConfig createTestConfig(Long id, String siteName, boolean isEnabled) {
        ScrapeConfig config = new ScrapeConfig();
        config.setId(id);
        config.setSiteName(siteName);
        config.setSiteUrl("https://example.com/" + siteName.toLowerCase());
        config.setCronExpression("0 0 6 * * *");
        config.setIsEnabled(isEnabled);
        return config;
    }

    @Test
    void getAllConfigs_全設定取得() throws Exception {
        List<ScrapeConfig> configs = Arrays.asList(
                createTestConfig(1L, "BANDAI", true),
                createTestConfig(2L, "TAKARA_TOMY", false)
        );
        when(scrapeConfigService.getAllConfigs()).thenReturn(configs);

        mockMvc.perform(get("/api/scrape/configs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].siteName").value("BANDAI"))
                .andExpect(jsonPath("$[1].siteName").value("TAKARA_TOMY"));
    }

    @Test
    void getConfigById_存在する場合() throws Exception {
        ScrapeConfig config = createTestConfig(1L, "BANDAI", true);
        when(scrapeConfigService.getConfigById(1L)).thenReturn(config);

        mockMvc.perform(get("/api/scrape/configs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.siteName").value("BANDAI"))
                .andExpect(jsonPath("$.isEnabled").value(true));
    }

    @Test
    void getConfigById_存在しない場合は404() throws Exception {
        when(scrapeConfigService.getConfigById(999L)).thenThrow(new RuntimeException("見つかりません"));

        mockMvc.perform(get("/api/scrape/configs/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void createConfig_正常作成() throws Exception {
        ScrapeConfig input = new ScrapeConfig();
        input.setSiteName("NEW_SITE");
        input.setSiteUrl("https://new-site.com");
        input.setCronExpression("0 0 6 * * *");
        input.setIsEnabled(true);

        ScrapeConfig created = createTestConfig(3L, "NEW_SITE", true);
        when(scrapeConfigService.createConfig(any(ScrapeConfig.class))).thenReturn(created);

        mockMvc.perform(post("/api/scrape/configs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.siteName").value("NEW_SITE"));
    }

    @Test
    void createConfig_無効なcron式で400() throws Exception {
        ScrapeConfig input = new ScrapeConfig();
        input.setSiteName("TEST");
        input.setSiteUrl("https://example.com");
        input.setCronExpression("invalid");
        input.setIsEnabled(true);

        when(scrapeConfigService.createConfig(any(ScrapeConfig.class)))
                .thenThrow(new IllegalArgumentException("無効なcron式です"));

        mockMvc.perform(post("/api/scrape/configs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void createConfig_重複サイト名で409() throws Exception {
        ScrapeConfig input = new ScrapeConfig();
        input.setSiteName("BANDAI");
        input.setSiteUrl("https://example.com");
        input.setCronExpression("0 0 6 * * *");
        input.setIsEnabled(true);

        when(scrapeConfigService.createConfig(any(ScrapeConfig.class)))
                .thenThrow(new RuntimeException("既に存在します"));

        mockMvc.perform(post("/api/scrape/configs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void updateConfig_正常更新() throws Exception {
        ScrapeConfig input = new ScrapeConfig();
        input.setSiteName("BANDAI_UPDATED");
        input.setSiteUrl("https://updated.com");
        input.setCronExpression("0 0 12 * * *");
        input.setIsEnabled(false);

        ScrapeConfig updated = createTestConfig(1L, "BANDAI_UPDATED", false);
        when(scrapeConfigService.updateConfig(eq(1L), any(ScrapeConfig.class))).thenReturn(updated);

        mockMvc.perform(put("/api/scrape/configs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.siteName").value("BANDAI_UPDATED"));
    }

    @Test
    void updateConfig_存在しないIDで404() throws Exception {
        ScrapeConfig input = new ScrapeConfig();
        input.setSiteName("TEST");
        input.setSiteUrl("https://example.com");
        input.setIsEnabled(true);

        when(scrapeConfigService.updateConfig(eq(999L), any(ScrapeConfig.class)))
                .thenThrow(new RuntimeException("見つかりません"));

        mockMvc.perform(put("/api/scrape/configs/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteConfig_正常削除() throws Exception {
        doNothing().when(scrapeConfigService).deleteConfig(1L);

        mockMvc.perform(delete("/api/scrape/configs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void deleteConfig_存在しないIDで404() throws Exception {
        doThrow(new RuntimeException("見つかりません")).when(scrapeConfigService).deleteConfig(999L);

        mockMvc.perform(delete("/api/scrape/configs/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void toggleEnabled_正常切り替え() throws Exception {
        ScrapeConfig toggled = createTestConfig(1L, "BANDAI", false);
        when(scrapeConfigService.toggleEnabled(1L)).thenReturn(toggled);

        mockMvc.perform(patch("/api/scrape/configs/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isEnabled").value(false));
    }

    @Test
    void toggleEnabled_存在しないIDで404() throws Exception {
        when(scrapeConfigService.toggleEnabled(999L))
                .thenThrow(new RuntimeException("見つかりません"));

        mockMvc.perform(patch("/api/scrape/configs/999/toggle"))
                .andExpect(status().isNotFound());
    }
}
