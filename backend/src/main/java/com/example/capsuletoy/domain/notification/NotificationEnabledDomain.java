package com.example.capsuletoy.domain.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationEnabledDomain {
    @Value("${notification.enabled}")
    private boolean notificationEnabled;

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }
}
