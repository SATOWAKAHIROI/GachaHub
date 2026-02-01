package com.example.capsuletoy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * スケジューラー設定クラス
 * Spring Schedulerを有効化し、定期実行タスクを利用可能にする
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
}
