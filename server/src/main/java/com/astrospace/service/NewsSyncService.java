package com.astrospace.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.astrospace.domain.Article;
import com.astrospace.util.ApiException;

@Service
public class NewsSyncService {

    private static final Logger log = LoggerFactory.getLogger(NewsSyncService.class);

    private final SpaceApiClient api;
    private final MongoTemplate mongo;
    private final CacheService cache;

    public NewsSyncService(SpaceApiClient api, MongoTemplate mongo, CacheService cache) {
        this.api = api;
        this.mongo = mongo;
        this.cache = cache;
    }

    /**
     * Pulls both sources independently (one failing does not block the other, like Promise.allSettled),
     * inserts articles we don't have yet (existing ones are left untouched, i.e. $setOnInsert) and drops
     * the cached news lists.
     *
     * @return number of articles considered
     * @throws ApiException 502 if both upstream APIs failed
     */
    public int sync() {
        Map<String, Article> byUrl = new LinkedHashMap<>();
        int failures = 0;

        try {
            api.fetchSpaceflight().forEach(n -> NewsNormalizer.fromSpaceflight(n)
                    .ifPresent(a -> byUrl.putIfAbsent(a.getUrl(), a)));
        } catch (Exception e) {
            failures++;
            log.warn("Spaceflight fetch failed: {}", e.getMessage());
        }

        try {
            api.fetchApod().flatMap(NewsNormalizer::fromApod)
                    .ifPresent(a -> byUrl.putIfAbsent(a.getUrl(), a));
        } catch (Exception e) {
            failures++;
            log.warn("NASA APOD fetch failed: {}", e.getMessage());
        }

        if (failures == 2) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Could not reach the upstream news APIs");
        }
        if (byUrl.isEmpty()) {
            return 0;
        }

        Instant now = Instant.now();
        BulkOperations bulk = mongo.bulkOps(BulkOperations.BulkMode.UNORDERED, Article.class);
        for (Article a : new ArrayList<>(byUrl.values())) {
            bulk.upsert(Query.query(Criteria.where("url").is(a.getUrl())), insertOnly(a, now));
        }
        bulk.execute();

        // New articles make every cached "latest" list stale
        cache.invalidate(NewsQueryService.KEY_PREFIX + "*");
        return byUrl.size();
    }

    private static Update insertOnly(Article a, Instant now) {
        Update u = new Update()
                .setOnInsert("likeCount", 0L)
                .setOnInsert("views", 0L)
                .setOnInsert("createdAt", now)
                .setOnInsert("updatedAt", now);
        setIfPresent(u, "title", a.getTitle());
        setIfPresent(u, "summary", a.getSummary());
        setIfPresent(u, "image", a.getImage());
        setIfPresent(u, "source", a.getSource());
        setIfPresent(u, "category", a.getCategory());
        setIfPresent(u, "author", a.getAuthor());
        setIfPresent(u, "publishedAt", a.getPublishedAt());
        return u; // "url" comes from the upsert filter itself
    }

    private static void setIfPresent(Update u, String field, Object value) {
        if (value != null) {
            u.setOnInsert(field, value);
        }
    }
}
