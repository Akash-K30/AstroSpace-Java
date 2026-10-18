package com.astrospace.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.astrospace.domain.User;
import com.astrospace.dto.ArticleResponse;
import com.astrospace.repository.ArticleRepository;
import com.astrospace.repository.NewsQueries;
import com.astrospace.util.ApiException;
import com.astrospace.util.Ids;
import com.mongodb.client.result.UpdateResult;

/**
 * Bookmarks live in User.bookmarks (array of article ObjectIds), as in the Node version.
 * $addToSet/$pull make add/remove atomic, and comparing real ObjectIds fixes the old
 * string-vs-ObjectId duplicate check.
 */
@Service
public class BookmarkService {

    private final MongoTemplate mongo;
    private final ArticleRepository articles;
    private final NewsQueries queries;
    private final UserService users;

    public BookmarkService(MongoTemplate mongo, ArticleRepository articles, NewsQueries queries, UserService users) {
        this.mongo = mongo;
        this.articles = articles;
        this.queries = queries;
        this.users = users;
    }

    public void add(String userId, String articleId) {
        Ids.require(articleId, "Article not found");
        if (!articles.existsById(articleId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Article not found");
        }
        UpdateResult result = mongo.updateFirst(
                userQuery(userId),
                new Update().addToSet("bookmarks", new ObjectId(articleId)),
                User.class);
        if (result.getModifiedCount() == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Already bookmarked");
        }
    }

    public void remove(String userId, String articleId) {
        ObjectId id = Ids.parseOrNull(articleId);
        if (id == null) {
            return; // nothing that could be bookmarked under this id
        }
        mongo.updateFirst(userQuery(userId), new Update().pull("bookmarks", id), User.class);
    }

    public List<ArticleResponse> list(String userId) {
        User user = users.require(userId);
        return queries.findByIdsNewestFirst(user.getBookmarks()).stream().map(ArticleResponse::from).toList();
    }

    private static Query userQuery(String userId) {
        return Query.query(Criteria.where("_id").is(new ObjectId(userId)));
    }
}
