package com.astrospace.dto;

import java.time.Instant;

import com.astrospace.domain.Article;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticleResponse(
        @JsonProperty("_id") String id,
        String title,
        String summary,
        String image,
        String url,
        String source,
        String category,
        Instant publishedAt,
        String author,
        long views,
        long likeCount,
        Instant createdAt,
        Instant updatedAt,
        Long commentCount,
        @JsonProperty("isLiked") Boolean isLiked,
        @JsonProperty("isBookmarked") Boolean isBookmarked) {

    public static ArticleResponse from(Article a) {
        return new ArticleResponse(a.getId(), a.getTitle(), a.getSummary(), a.getImage(), a.getUrl(), a.getSource(),
                a.getCategory(), a.getPublishedAt(), a.getAuthor(), a.getViews(), a.getLikeCount(),
                a.getCreatedAt(), a.getUpdatedAt(), null, null, null);
    }

    public ArticleResponse decorate(long commentCount, boolean liked, boolean bookmarked) {
        return new ArticleResponse(id, title, summary, image, url, source, category, publishedAt, author, views,
                likeCount, createdAt, updatedAt, commentCount, liked, bookmarked);
    }
}
