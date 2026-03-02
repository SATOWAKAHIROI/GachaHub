package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.service.scrapeConfig.ScrapeConfigService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
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
}
