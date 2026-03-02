package com.example.capsuletoy.controller;

import com.example.capsuletoy.scraper.BandaiScraper;
import com.example.capsuletoy.scraper.TakaraTomyScraper;
import com.example.capsuletoy.service.scraping.ScrapeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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
    void scrapeTakaraTomy_例外発生時は500エラー() throws Exception {
        when(scrapeService.executeScraping(any(TakaraTomyScraper.class), eq("TAKARA_TOMY_ARTS")))
                .thenThrow(new RuntimeException("Connection timeout"));

        mockMvc.perform(post("/api/scrape/takaratomy"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }
}
