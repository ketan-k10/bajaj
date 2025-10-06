package com.example.webhook.controller;

import com.example.webhook.dto.GenerateWebhookRequest;
import com.example.webhook.dto.GenerateWebhookResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/hiring")
public class GenerateWebhookController {

    private static final Logger log = LoggerFactory.getLogger(GenerateWebhookController.class);

    // In-memory secret for demo/testing. In production, store securely (KMS/Env)
    private static final String SECRET = System.getProperty("app.jwt.secret", System.getenv().getOrDefault("APP_JWT_SECRET", "very-strong-secret-change-me"));

    @PostMapping(path = "/generateWebhook/JAVA", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GenerateWebhookResponse generate(@RequestBody GenerateWebhookRequest req, HttpServletRequest servletRequest) {
        log.info("Received generateWebhook request for regNo={}", req.getRegNo());

        // Build webhook URL pointing back to this server's testWebhook endpoint
        String scheme = servletRequest.getScheme();
        String host = servletRequest.getServerName();
        int port = servletRequest.getServerPort();
        String webhook = String.format("%s://%s:%d/hiring/testWebhook/JAVA", scheme, host, port);

        // create JWT token with simple claims
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
        Instant now = Instant.now();
        String jws = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject("webhook-user")
                .claim("regNo", req.getRegNo())
                .claim("name", req.getName())
                .claim("email", req.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(60*60))) // 1 hour expiry
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("Generated token for regNo={}, webhook={}", req.getRegNo(), webhook);
        return new GenerateWebhookResponse(webhook, jws);
    }
}
