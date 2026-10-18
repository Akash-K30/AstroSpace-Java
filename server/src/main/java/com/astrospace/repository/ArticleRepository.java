package com.astrospace.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.astrospace.domain.Article;

public interface ArticleRepository extends MongoRepository<Article, String> {
}
