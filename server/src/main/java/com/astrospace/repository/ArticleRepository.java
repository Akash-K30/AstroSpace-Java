package com.astrospace.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.astrospace.domain.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
}
