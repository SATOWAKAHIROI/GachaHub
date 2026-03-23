package com.example.capsuletoy.domain.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationFromAddressDomain {
    @Value("${notification.from-address}")
    private String fromAddress;

    public String getFromAddress() {
        return fromAddress;
    }
}
