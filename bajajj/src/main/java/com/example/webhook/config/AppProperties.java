package com.example.webhook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String generateWebhookUrl;
    private String finalQuery;
    private String finalQueryFilePath;
    private String jwtSecret = "change-me";
    private String authHeaderPrefix = "";

    public String getGenerateWebhookUrl() {
        return generateWebhookUrl;
    }
    public void setGenerateWebhookUrl(String generateWebhookUrl) {
        this.generateWebhookUrl = generateWebhookUrl;
    }

    public String getFinalQuery() {
        return finalQuery;
    }
    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQueryFilePath() {
        return finalQueryFilePath;
    }
    public void setFinalQueryFilePath(String finalQueryFilePath) {
        this.finalQueryFilePath = finalQueryFilePath;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String getAuthHeaderPrefix() {
        return authHeaderPrefix;
    }
    public void setAuthHeaderPrefix(String authHeaderPrefix) {
        this.authHeaderPrefix = authHeaderPrefix;
    }
}
