package com.example.capsuletoy.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scrape_configs")
public class ScrapeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name", nullable = false)
    private String siteName;

    @Column(name = "site_url", nullable = false, columnDefinition = "TEXT")
    private String siteUrl;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Column(name = "last_scraped_at")
    private LocalDateTime lastScrapedAt;

    // Constructors
    public ScrapeConfig() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public LocalDateTime getLastScrapedAt() {
        return lastScrapedAt;
    }

    public void setLastScrapedAt(LocalDateTime lastScrapedAt) {
        this.lastScrapedAt = lastScrapedAt;
    }
}
