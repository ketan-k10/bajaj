package com.example.webhook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateWebhookResponse {
    private String webhook;

    @JsonProperty("accessToken")
    private String accessToken;

    public GenerateWebhookResponse() {}

    public GenerateWebhookResponse(String webhook, String accessToken) {
        this.webhook = webhook;
        this.accessToken = accessToken;
    }

    public String getWebhook() { return webhook; }
    public void setWebhook(String webhook) { this.webhook = webhook; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
