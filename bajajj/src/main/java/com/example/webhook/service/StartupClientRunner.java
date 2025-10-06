package com.example.webhook.service;

import com.example.webhook.config.AppProperties;
import com.example.webhook.dto.GenerateWebhookRequest;
import com.example.webhook.dto.GenerateWebhookResponse;
import com.example.webhook.dto.SubmissionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class StartupClientRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupClientRunner.class);

    private final AppProperties props;
    private final WebClient webClient = WebClient.builder().build();

    public StartupClientRunner(AppProperties props) {
        this.props = props;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("StartupClientRunner beginning flow...");

        String generateUrl = props.getGenerateWebhookUrl();
        if (generateUrl == null || generateUrl.isBlank()) {
            log.warn("app.generateWebhookUrl not configured. Skipping client flow.");
            return;
        }

        GenerateWebhookRequest request = new GenerateWebhookRequest("John Doe","REG12347","john@example.com");
        try {
            GenerateWebhookResponse resp = callGenerateWebhook(generateUrl, request);
            log.info("generateWebhook response: webhook={}, tokenLength={}", resp.getWebhook(), resp.getAccessToken() == null ? 0 : resp.getAccessToken().length());

            String finalQuery = loadFinalQuery();
            if (finalQuery == null || finalQuery.isBlank()) {
                log.warn("No finalQuery provided. Skipping submission.");
                return;
            }

            // Submit to returned webhook
            submitFinalQuery(resp.getWebhook(), resp.getAccessToken(), props.getAuthHeaderPrefix(), finalQuery);
        } catch (Exception e) {
            log.error("Startup flow failed: {}", e.getMessage(), e);
        }
    }

    private GenerateWebhookResponse callGenerateWebhook(String url, GenerateWebhookRequest request) {
        Mono<GenerateWebhookResponse> mono = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GenerateWebhookResponse.class);
        return mono.block();
    }

    private void submitFinalQuery(String webhookUrl, String jwt, String prefix, String finalQuery) {
        String header = (prefix == null ? "" : prefix) + jwt;
        SubmissionRequest submission = new SubmissionRequest(finalQuery);

        Mono<String> mono = webClient.post()
                .uri(webhookUrl)
                .header(HttpHeaders.AUTHORIZATION, header)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(submission)
                .retrieve()
                .bodyToMono(String.class);

        String resp = mono.block();
        log.info("Submission response: {}", resp);
    }

    private String loadFinalQuery() {
        if (props.getFinalQuery() != null && !props.getFinalQuery().isBlank()) {
            log.info("Using finalQuery from properties.");
            return props.getFinalQuery().trim();
        }
        if (props.getFinalQueryFilePath() != null && !props.getFinalQueryFilePath().isBlank()) {
            try {
                return Files.readString(Path.of(props.getFinalQueryFilePath())).trim();
            } catch (IOException e) {
                log.warn("Failed to read finalQuery file: {}", e.getMessage());
            }
        }
        return null;
    }
}
