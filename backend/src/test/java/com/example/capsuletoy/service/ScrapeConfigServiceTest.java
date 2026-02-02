package com.example.capsuletoy.service;

import com.example.capsuletoy.model.ScrapeConfig;
import com.example.capsuletoy.repository.ScrapeConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void createConfig_正常に作成() {
        ScrapeConfig config = createTestConfig(null, "NEW_SITE", true);
        ScrapeConfig saved = createTestConfig(3L, "NEW_SITE", true);

        when(scrapeConfigRepository.findBySiteName("NEW_SITE")).thenReturn(null);
        when(scrapeConfigRepository.save(config)).thenReturn(saved);

        ScrapeConfig result = scrapeConfigService.createConfig(config);

        assertEquals(3L, result.getId());
        assertEquals("NEW_SITE", result.getSiteName());
    }

    @Test
    void createConfig_同名サイト存在時は例外() {
        ScrapeConfig config = createTestConfig(null, "BANDAI", true);
        ScrapeConfig existing = createTestConfig(1L, "BANDAI", true);

        when(scrapeConfigRepository.findBySiteName("BANDAI")).thenReturn(existing);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> scrapeConfigService.createConfig(config));

        assertTrue(exception.getMessage().contains("既に存在します"));
    }

    @Test
    void createConfig_無効なcron式で例外() {
        ScrapeConfig config = new ScrapeConfig();
        config.setSiteName("TEST");
        config.setSiteUrl("https://example.com");
        config.setCronExpression("invalid cron");
        config.setIsEnabled(true);

        assertThrows(IllegalArgumentException.class,
                () -> scrapeConfigService.createConfig(config));
    }

    @Test
    void createConfig_cron式が空の場合はバリデーションスキップ() {
        ScrapeConfig config = new ScrapeConfig();
        config.setSiteName("TEST");
        config.setSiteUrl("https://example.com");
        config.setCronExpression("");
        config.setIsEnabled(true);

        when(scrapeConfigRepository.findBySiteName("TEST")).thenReturn(null);
        when(scrapeConfigRepository.save(any(ScrapeConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        ScrapeConfig result = scrapeConfigService.createConfig(config);

        assertEquals("TEST", result.getSiteName());
    }

    @Test
    void updateConfig_正常に更新() {
        ScrapeConfig existing = createTestConfig(1L, "BANDAI", true);
        ScrapeConfig updated = new ScrapeConfig();
        updated.setSiteName("BANDAI_UPDATED");
        updated.setSiteUrl("https://new-url.com");
        updated.setCronExpression("0 0 12 * * *");
        updated.setIsEnabled(false);

        when(scrapeConfigRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(scrapeConfigRepository.save(any(ScrapeConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        ScrapeConfig result = scrapeConfigService.updateConfig(1L, updated);

        assertEquals("BANDAI_UPDATED", result.getSiteName());
        assertEquals("https://new-url.com", result.getSiteUrl());
        assertEquals("0 0 12 * * *", result.getCronExpression());
        assertFalse(result.getIsEnabled());
    }

    @Test
    void updateConfig_無効なcron式で例外() {
        ScrapeConfig existing = createTestConfig(1L, "BANDAI", true);
        ScrapeConfig updated = new ScrapeConfig();
        updated.setSiteName("BANDAI");
        updated.setSiteUrl("https://example.com");
        updated.setCronExpression("bad cron");
        updated.setIsEnabled(true);

        when(scrapeConfigRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> scrapeConfigService.updateConfig(1L, updated));
    }

    @Test
    void deleteConfig_正常に削除() {
        ScrapeConfig config = createTestConfig(1L, "BANDAI", true);
        when(scrapeConfigRepository.findById(1L)).thenReturn(Optional.of(config));

        scrapeConfigService.deleteConfig(1L);

        verify(scrapeConfigRepository).deleteById(1L);
    }

    @Test
    void deleteConfig_存在しないIDで例外() {
        when(scrapeConfigRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> scrapeConfigService.deleteConfig(999L));
    }

    @Test
    void toggleEnabled_有効から無効に切り替え() {
        ScrapeConfig config = createTestConfig(1L, "BANDAI", true);
        when(scrapeConfigRepository.findById(1L)).thenReturn(Optional.of(config));
        when(scrapeConfigRepository.save(any(ScrapeConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        ScrapeConfig result = scrapeConfigService.toggleEnabled(1L);

        assertFalse(result.getIsEnabled());
    }

    @Test
    void toggleEnabled_無効から有効に切り替え() {
        ScrapeConfig config = createTestConfig(1L, "BANDAI", false);
        when(scrapeConfigRepository.findById(1L)).thenReturn(Optional.of(config));
        when(scrapeConfigRepository.save(any(ScrapeConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        ScrapeConfig result = scrapeConfigService.toggleEnabled(1L);

        assertTrue(result.getIsEnabled());
    }
}
