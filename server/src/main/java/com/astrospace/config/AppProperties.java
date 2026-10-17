package com.astrospace.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Cookie cookie, Cors cors, RateLimit rateLimit, Nasa nasa, Sync sync) {

    public record Jwt(String secret, Duration expiration) {
    }

    public record Cookie(boolean secure, String sameSite) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    public record RateLimit(boolean enabled) {
    }

    public record Nasa(String apiKey) {
    }

    public record Sync(boolean enabled) {
    }
}
