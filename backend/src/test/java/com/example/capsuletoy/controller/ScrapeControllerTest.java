package com.example.capsuletoy.controller;

import com.example.capsuletoy.model.ScrapeLog;
import com.example.capsuletoy.scraper.BandaiScraper;
import com.example.capsuletoy.scraper.TakaraTomyScraper;
import com.example.capsuletoy.service.ScrapeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ScrapeControllerのエンドポイントテスト
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class ScrapeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScrapeService scrapeService;

    @MockitoBean
    private BandaiScraper bandaiScraper;

    @MockitoBean
    private TakaraTomyScraper takaraTomyScraper;

    private ScrapeLog createTestLog(Long id, String site, String status, int productsFound) {
        ScrapeLog log = new ScrapeLog();
        log.setId(id);
        log.setTargetSite(site);
        log.setStatus(status);
        log.setProductsFound(productsFound);
        log.setExecutedAt(LocalDateTime.now());
        return log;
    }

    @Test
    void scrapeBandai_正常実行() throws Exception {
        when(scrapeService.executeScraping(any(BandaiScraper.class), eq("BANDAI_GASHAPON")))
                .thenReturn(5);

        mockMvc.perform(post("/api/scrape/bandai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.site").value("BANDAI_GASHAPON"))
                .andExpect(jsonPath("$.productsScraped").value(5));
    }

    @Test
    void scrapeBandai_例外発生時は500エラー() throws Exception {
        when(scrapeService.executeScraping(any(BandaiScraper.class), eq("BANDAI_GASHAPON")))
                .thenThrow(new RuntimeException("Scraping failed"));

        mockMvc.perform(post("/api/scrape/bandai"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.site").value("BANDAI_GASHAPON"));
    }

    @Test
    void scrapeTakaraTomy_正常実行() throws Exception {
        when(scrapeService.executeScraping(any(TakaraTomyScraper.class), eq("TAKARA_TOMY_ARTS")))
                .thenReturn(3);

        mockMvc.perform(post("/api/scrape/takaratomy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.site").value("TAKARA_TOMY_ARTS"))
                .andExpect(jsonPath("$.productsScraped").value(3));
    }

    @Test
    void scrapeTakaraTomy_例外発生時は500エラー() throws Exception {
        when(scrapeService.executeScraping(any(TakaraTomyScraper.class), eq("TAKARA_TOMY_ARTS")))
                .thenThrow(new RuntimeException("Connection timeout"));

        mockMvc.perform(post("/api/scrape/takaratomy"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void getScrapeLogs_ログ取得() throws Exception {
        List<ScrapeLog> logs = Arrays.asList(
                createTestLog(1L, "BANDAI_GASHAPON", "SUCCESS", 10),
                createTestLog(2L, "TAKARA_TOMY_ARTS", "FAILURE", 0)
        );
        when(scrapeService.getRecentScrapeLogs(10)).thenReturn(logs);

        mockMvc.perform(get("/api/scrape/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].targetSite").value("BANDAI_GASHAPON"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[1].status").value("FAILURE"));
    }

    @Test
    void getScrapeLogs_limit指定() throws Exception {
        List<ScrapeLog> logs = List.of(createTestLog(1L, "BANDAI_GASHAPON", "SUCCESS", 5));
        when(scrapeService.getRecentScrapeLogs(5)).thenReturn(logs);

        mockMvc.perform(get("/api/scrape/logs").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getScrapeLogsBySite_サイト別ログ取得() throws Exception {
        List<ScrapeLog> logs = List.of(createTestLog(1L, "BANDAI_GASHAPON", "SUCCESS", 10));
        when(scrapeService.getScrapeLogsByTargetSite("BANDAI_GASHAPON")).thenReturn(logs);

        mockMvc.perform(get("/api/scrape/logs/BANDAI_GASHAPON"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].targetSite").value("BANDAI_GASHAPON"));
    }

    @Test
    void getScrapeStatus_ステータス取得() throws Exception {
        ScrapeLog latestLog = createTestLog(1L, "BANDAI_GASHAPON", "SUCCESS", 10);
        when(scrapeService.getRecentScrapeLogs(1)).thenReturn(List.of(latestLog));

        mockMvc.perform(get("/api/scrape/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.supportedSites", hasSize(2)))
                .andExpect(jsonPath("$.lastStatus").value("SUCCESS"));
    }

    @Test
    void getScrapeStatus_ログなしの場合() throws Exception {
        when(scrapeService.getRecentScrapeLogs(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/scrape/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.supportedSites", hasSize(2)))
                .andExpect(jsonPath("$.lastExecution").doesNotExist());
    }
}
