package com.example.webhook.controller;

import com.example.webhook.dto.SubmissionRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;

@RestController
@RequestMapping("/hiring")
public class TestWebhookController {

    private static final Logger log = LoggerFactory.getLogger(TestWebhookController.class);

    private static final String SECRET = System.getProperty("app.jwt.secret", System.getenv().getOrDefault("APP_JWT_SECRET", "very-strong-secret-change-me"));

    @PostMapping(path = "/testWebhook/JAVA", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> receiveSubmission(@RequestHeader(value = "Authorization", required = false) String authorization,
                                               @RequestBody SubmissionRequest submission) {
        log.info("Received testWebhook submission. Authorization present: {}", authorization != null);

        if (authorization == null || authorization.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"Missing Authorization header\"}");
        }

        // The header may be "Bearer <token>" or raw token. strip Bearer if present.
        String token = authorization.trim();
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }

        try {
            Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.info("Token verified. Claims: {}", claims.getBody());
        } catch (Exception e) {
            log.warn("Token verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"Invalid token\"}");
        }

        log.info("Final query received: {} ({} chars)", submission.getFinalQuery(), submission.getFinalQuery() == null ? 0 : submission.getFinalQuery().length());
        // For demonstration return the received query and status
        return ResponseEntity.ok().body("{\"status\":\"received\",\"message\":\"Query accepted\"}");
    }
}
