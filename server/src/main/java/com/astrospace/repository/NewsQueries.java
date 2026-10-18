package com.astrospace.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.astrospace.domain.Article;
import com.astrospace.domain.Comment;

/** Dynamic queries that don't fit a derived repository method (filters, $text search, aggregation). */
@Repository
public class NewsQueries {

    private final MongoTemplate mongo;

    public NewsQueries(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public List<Article> search(String category, String q, boolean popular, int page, int limit) {
        Query query = new Query();
        if (StringUtils.hasText(category)) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        if (StringUtils.hasText(q)) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matchingAny(q.trim().split("\\s+")));
        }
        Sort sort = popular
                ? Sort.by(Sort.Direction.DESC, "likeCount", "publishedAt")
                : Sort.by(Sort.Direction.DESC, "publishedAt");
        query.with(sort).skip((long) (page - 1) * limit).limit(limit);
        return mongo.find(query, Article.class);
    }

    public List<Article> textSearch(String q, int limit) {
        Query query = new Query(TextCriteria.forDefaultLanguage().matchingAny(q.trim().split("\\s+")));
        query.limit(limit);
        return mongo.find(query, Article.class);
    }

    public List<Article> findByIdsNewestFirst(Collection<ObjectId> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        Query query = new Query(Criteria.where("_id").in(ids)).with(Sort.by(Sort.Direction.DESC, "publishedAt"));
        return mongo.find(query, Article.class);
    }

    /** article id (hex) -> number of comments */
    public Map<String, Long> commentCounts(Collection<ObjectId> articleIds) {
        Map<String, Long> counts = new HashMap<>();
        if (articleIds.isEmpty()) {
            return counts;
        }
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("article").in(articleIds)),
                Aggregation.group("article").count().as("count"));
        List<Document> rows = mongo.aggregate(agg, Comment.class, Document.class).getMappedResults();
        for (Document row : rows) {
            ObjectId id = row.getObjectId("_id");
            Number count = row.get("count", Number.class);
            if (id != null && count != null) {
                counts.put(id.toHexString(), count.longValue());
            }
        }
        return counts;
    }
}
