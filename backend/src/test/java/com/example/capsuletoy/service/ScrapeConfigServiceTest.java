package com.example.capsuletoy.service;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;
import com.example.capsuletoy.service.scrapeConfig.ScrapeConfigService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ScrapeConfigServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
class ScrapeConfigServiceTest {

    @InjectMocks
    private ScrapeConfigService scrapeConfigService;

    @Mock
    private ScrapeConfigRepository scrapeConfigRepository;

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
    void getAllConfigs_全設定を返す() {
        List<ScrapeConfig> configs = Arrays.asList(
                createTestConfig(1L, "BANDAI", true),
                createTestConfig(2L, "TAKARA_TOMY", false)
        );
        when(scrapeConfigRepository.findAll()).thenReturn(configs);

        List<ScrapeConfig> result = scrapeConfigService.getAllConfigs();

        assertEquals(2, result.size());
    }

    @Test
    void getEnabledConfigs_有効な設定のみ返す() {
        List<ScrapeConfig> enabled = List.of(createTestConfig(1L, "BANDAI", true));
        when(scrapeConfigRepository.findByIsEnabledTrue()).thenReturn(enabled);

        List<ScrapeConfig> result = scrapeConfigService.getEnabledConfigs();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsEnabled());
    }

    @Test
    void getConfigById_存在する場合() {
        ScrapeConfig config = createTestConfig(1L, "BANDAI", true);
        when(scrapeConfigRepository.findById(1L)).thenReturn(Optional.of(config));

        ScrapeConfig result = scrapeConfigService.getConfigById(1L);

        assertEquals("BANDAI", result.getSiteName());
    }

    @Test
    void getConfigById_存在しない場合は例外() {
        when(scrapeConfigRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> scrapeConfigService.getConfigById(999L));

        assertTrue(exception.getMessage().contains("見つかりません"));
    }
}
