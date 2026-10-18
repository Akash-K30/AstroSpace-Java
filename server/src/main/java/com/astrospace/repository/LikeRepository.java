package com.astrospace.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.astrospace.domain.Like;import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {

    List<Like> findByUser(ObjectId user);

    List<Like> findByUserAndArticleIn(ObjectId user, Collection<ObjectId> articles);

    long countByUser(ObjectId user);
}
