package com.astrospace.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.astrospace.domain.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByArticleOrderByCreatedAtDesc(ObjectId article);

    List<Comment> findByUserOrderByCreatedAtDesc(ObjectId user);

    long countByUser(ObjectId user);
}
