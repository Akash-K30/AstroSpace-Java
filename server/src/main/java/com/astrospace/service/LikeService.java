package com.astrospace.service;

import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.astrospace.domain.Article;
import com.astrospace.domain.Like;
import com.astrospace.dto.Responses.LikeResult;
import com.astrospace.repository.ArticleRepository;
import com.astrospace.util.ApiException;
import com.astrospace.util.Ids;
import com.mongodb.client.result.DeleteResult;

@Service
public class LikeService {

    private final MongoTemplate mongo;
    private final ArticleRepository articles;

    public LikeService(MongoTemplate mongo, ArticleRepository articles) {
        this.mongo = mongo;
        this.articles = articles;
    }

    /**
     * Toggle: if a like exists it is removed (-1), otherwise created (+1). The counter only moves when a
     * like document was really deleted/inserted, so double-clicks and races can't push likeCount out of sync.
     * Unlike the Node version, liking an article that doesn't exist is a 404.
     */
    public LikeResult toggle(String userId, String articleId) {
        ObjectId articleOid = Ids.require(articleId, "Article not found");
        if (!articles.existsById(articleId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Article not found");
        }
        ObjectId userOid = new ObjectId(userId);

        DeleteResult deleted = mongo.remove(
                Query.query(Criteria.where("user").is(userOid).and("article").is(articleOid)), Like.class);

        boolean liked;
        long delta;
        if (deleted.getDeletedCount() > 0) {
            liked = false;
            delta = -1;
        } else {
            liked = true;
            try {
                mongo.insert(new Like(userOid, articleOid));
                delta = 1;
            } catch (DuplicateKeyException e) { // concurrent request already inserted it
                delta = 0;
            }
        }

        Article updated = mongo.findAndModify(
                Query.query(Criteria.where("_id").is(articleOid)),
                new Update().inc("likeCount", delta),
                FindAndModifyOptions.options().returnNew(true),
                Article.class);
        return new LikeResult(liked, updated == null ? 0 : updated.getLikeCount());
    }
}
