package com.astrospace.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

import com.astrospace.domain.Article;
import com.astrospace.domain.Comment;
import com.astrospace.domain.Like;
import com.astrospace.domain.User;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);


    @Bean
    ApplicationRunner ensureIndexes(MongoTemplate mongo) {
        return args -> {
            ensure(mongo, User.class, new Index().on("email", Sort.Direction.ASC).unique().named("email_1"));

            ensure(mongo, Article.class, new Index().on("url", Sort.Direction.ASC).unique().named("url_1"));
            ensure(mongo, Article.class, new Index().on("publishedAt", Sort.Direction.DESC).named("publishedAt_-1"));
            ensure(mongo, Article.class, new Index()
                    .on("likeCount", Sort.Direction.DESC)
                    .on("publishedAt", Sort.Direction.DESC)
                    .named("likeCount_-1_publishedAt_-1"));
            ensure(mongo, Article.class, new TextIndexDefinition.TextIndexDefinitionBuilder()
                    .onFields("title", "summary")
                    .named("title_text_summary_text")
                    .build());

            ensure(mongo, Like.class, new Index()
                    .on("user", Sort.Direction.ASC)
                    .on("article", Sort.Direction.ASC)
                    .unique()
                    .named("user_1_article_1"));

            // New (not in the Node version): the two comment lookups the API performs
            ensure(mongo, Comment.class, new Index()
                    .on("article", Sort.Direction.ASC)
                    .on("createdAt", Sort.Direction.DESC)
                    .named("article_1_createdAt_-1"));
            ensure(mongo, Comment.class, new Index()
                    .on("user", Sort.Direction.ASC)
                    .on("createdAt", Sort.Direction.DESC)
                    .named("user_1_createdAt_-1"));
        };
    }

    private void ensure(MongoTemplate mongo, Class<?> type, IndexDefinition definition) {
        try {
            mongo.indexOps(type).createIndex(definition);
        } catch (Exception e) {
            log.warn("Could not ensure index on {}: {}", type.getSimpleName(), e.getMessage());
        }
    }
}
