package com.astrospace.dto;

import java.util.List;
import java.util.Map;

/** The user-independent part of a news list; this is what gets cached in Redis. */
public record NewsPage(List<ArticleResponse> articles, Map<String, Long> commentCountMap) {
}
