package com.astrospace.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.astrospace.domain.Article;
import com.astrospace.dto.ArticleResponse;
import com.astrospace.dto.NewsPage;
import com.astrospace.repository.NewsQueries;

/**
 * The cacheable, user-independent half of the news list (articles + comment counts), cached 60 s in Redis.
 * Per-user flags (isLiked / isBookmarked) are added afterwards in NewsService so one cache entry serves everyone.
 * Kept as a separate bean from NewsService so the cache logic is easy to reason about and to test in isolation.
 */
@Service
public class NewsQueryService {

    static final Duration TTL = Duration.ofSeconds(60);
    static final String KEY_PREFIX = "news:list:";

    private final NewsQueries queries;
    private final CacheService cache;

    public NewsQueryService(NewsQueries queries, CacheService cache) {
        this.queries = queries;
        this.cache = cache;
    }

    public NewsPage getPage(String category, String q, boolean popular, int page, int limit) {
        String key = "%s%s:%s:%s:%d:%d".formatted(
                KEY_PREFIX,
                blankTo(category, "all"),
                blankTo(q, "none"),
                popular ? "popular" : "latest",
                page,
                limit);

        return cache.get(key, NewsPage.class).orElseGet(() -> {
            NewsPage fresh = load(category, q, popular, page, limit);
            if (!fresh.articles().isEmpty()) { // like Express: empty results are not cached
                cache.put(key, fresh, TTL);
            }
            return fresh;
        });
    }

    private NewsPage load(String category, String q, boolean popular, int page, int limit) {
        List<Article> articles = queries.search(category, q, popular, page, limit);
        if (articles.isEmpty()) {
            return new NewsPage(List.of(), Map.of());
        }
        List<ObjectId> ids = articles.stream().map(a -> new ObjectId(a.getId())).toList();
        return new NewsPage(
                articles.stream().map(ArticleResponse::from).toList(),
                queries.commentCounts(ids));
    }

    private static String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
