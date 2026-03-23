package com.example.capsuletoy.domain.crone;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

@Component
public class CroneValidater {
    /**
     * cron式のバリデーション
     */
    public void validateCronExpression(String cronExpression) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("無効なcron式です: " + cronExpression);
        }
    }
}
