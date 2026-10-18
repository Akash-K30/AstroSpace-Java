package com.astrospace.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.astrospace.domain.Like;
import com.astrospace.dto.ArticleResponse;
import com.astrospace.dto.NewsPage;
import com.astrospace.repository.LikeRepository;
import com.astrospace.repository.NewsQueries;
import com.astrospace.repository.UserRepository;
import com.astrospace.util.ApiException;
import com.astrospace.util.Ids;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Service
public class NewsService {

    static final int MAX_LIMIT = 50;
    static final int MAX_QUERY_LENGTH = 100;
    private static final int SEARCH_LIMIT = 50;

    private final NewsQueryService newsQuery;
    private final NewsQueries queries;
    private final UserRepository users;
    private final LikeRepository likes;

    public NewsService(NewsQueryService newsQuery, NewsQueries queries, UserRepository users, LikeRepository likes) {
        this.newsQuery = newsQuery;
        this.queries = queries;
        this.users = users;
        this.likes = likes;
    }

    /** @param userId the logged-in user's id, or null for anonymous visitors */
    public List<ArticleResponse> list(String userId, String category, String q, String sort, int page, int limit) {
        int safePage = Math.max(1, page);
        int safeLimit = Math.clamp(limit, 1, MAX_LIMIT);
        String safeQuery = q == null ? null : q.substring(0, Math.min(q.length(), MAX_QUERY_LENGTH));

        NewsPage shared = newsQuery.getPage(category, safeQuery, "popular".equals(sort), safePage, safeLimit);
        if (shared.articles().isEmpty()) {
            return List.of();
        }

        Set<String> liked = new HashSet<>();
        Set<String> bookmarked = new HashSet<>();
        ObjectId uid = Ids.parseOrNull(userId);
        if (uid != null) {
            users.findById(userId).ifPresent(u -> bookmarked.addAll(Ids.hex(u.getBookmarks())));
            List<ObjectId> articleIds = shared.articles().stream().map(a -> new ObjectId(a.id())).toList();
            liked.addAll(likes.findByUserAndArticleIn(uid, articleIds).stream()
                    .map(Like::getArticle)
                    .map(ObjectId::toHexString)
                    .collect(Collectors.toSet()));
        }

        return shared.articles().stream()
                .map(a -> a.decorate(
                        shared.commentCountMap().getOrDefault(a.id(), 0L),
                        liked.contains(a.id()),
                        bookmarked.contains(a.id())))
                .toList();
    }

    public List<ArticleResponse> search(String q) {
        if (!StringUtils.hasText(q)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Query parameter 'q' is required");
        }
        String safe = q.substring(0, Math.min(q.length(), MAX_QUERY_LENGTH));
        return queries.textSearch(safe, SEARCH_LIMIT).stream().map(ArticleResponse::from).toList();
    }
}
