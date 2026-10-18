package com.astrospace.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import com.astrospace.domain.Article;
import com.fasterxml.jackson.databind.JsonNode;

/** Maps the two upstream payloads onto Article (port of utils/normalize.js). */
public final class NewsNormalizer {

    private NewsNormalizer() {
    }

    /** Spaceflight News API v4 article. Returns empty if it has no title/url (required fields). */
    public static Optional<Article> fromSpaceflight(JsonNode n) {
        return build(n, "title", "summary", "image_url", "url", "published_at", "Spaceflight");
    }


    public static Optional<Article> fromApod(JsonNode n) {
        if (n == null || n.isNull() || n.isMissingNode()) {
            return Optional.empty();
        }
        String hd = text(n, "hdurl");
        Article a = new Article();
        a.setTitle(text(n, "title"));
        a.setSummary(text(n, "explanation"));
        a.setImage(text(n, "url"));
        a.setUrl(hd != null ? hd : text(n, "url"));
        a.setSource("NASA");
        a.setPublishedAt(parseInstant(text(n, "date")));
        return valid(a);
    }

    private static Optional<Article> build(JsonNode n, String title, String summary, String image, String url,
                                           String published, String source) {
        if (n == null || n.isNull() || n.isMissingNode()) {
            return Optional.empty();
        }
        Article a = new Article();
        a.setTitle(text(n, title));
        a.setSummary(text(n, summary));
        a.setImage(text(n, image));
        a.setUrl(text(n, url));
        a.setSource(source);
        a.setPublishedAt(parseInstant(text(n, published)));
        return valid(a);
    }

    private static Optional<Article> valid(Article a) {
        return a.getTitle() == null || a.getUrl() == null ? Optional.empty() : Optional.of(a);
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) {
            return null;
        }
        String s = v.asText();
        return s == null || s.isBlank() ? null : s;
    }

    /** Accepts full ISO timestamps ("2026-08-12T09:00:00Z") and plain dates ("2026-08-12", used by APOD). */
    static Instant parseInstant(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ignored) {
            // fall through to date-only
        }
        try {
            return LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
