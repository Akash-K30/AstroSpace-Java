package com.astrospace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import com.astrospace.config.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;

/** Thin client for the two upstream APIs (spaceflight.service.js + nasa.service.js). Throws on failure. */
@Component
public class SpaceApiClient {

    private static final Logger log = LoggerFactory.getLogger(SpaceApiClient.class);
    private static final String SPACEFLIGHT_URL = "https://api.spaceflightnewsapi.net/v4/articles";
    private static final String APOD_URL = "https://api.nasa.gov/planetary/apod?api_key={key}";

    private final RestClient http;
    private final String nasaKey;

    public SpaceApiClient(RestClient.Builder builder, AppProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(10_000);
        this.http = builder.requestFactory(factory).build();

        String key = props.nasa() == null ? null : props.nasa().apiKey();
        if (!StringUtils.hasText(key)) {
            log.warn("NASA_KEY is not set, falling back to DEMO_KEY (heavily rate limited)");
            key = "DEMO_KEY";
        }
        this.nasaKey = key;
    }

    public List<JsonNode> fetchSpaceflight() {
        JsonNode body = http.get().uri(SPACEFLIGHT_URL).retrieve().body(JsonNode.class);
        List<JsonNode> results = new ArrayList<>();
        if (body != null && body.path("results").isArray()) {
            body.path("results").forEach(results::add);
        }
        return results;
    }

    public Optional<JsonNode> fetchApod() {
        JsonNode body = http.get().uri(APOD_URL, nasaKey).retrieve().body(JsonNode.class);
        return Optional.ofNullable(body).filter(n -> !n.isNull() && !n.isMissingNode());
    }
}
