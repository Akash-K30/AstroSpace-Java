package com.astrospace.service;

import java.util.List;

import com.astrospace.domain.Like;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.astrospace.domain.User;
import com.astrospace.dto.ArticleResponse;
import com.astrospace.dto.Responses.Dashboard;
import com.astrospace.dto.Responses.Profile;
import com.astrospace.dto.Responses.Stats;
import com.astrospace.repository.CommentRepository;
import com.astrospace.repository.LikeRepository;
import com.astrospace.repository.NewsQueries;

@Service
public class DashboardService {

    private final UserService users;
    private final LikeRepository likes;
    private final CommentRepository comments;
    private final NewsQueries queries;

    public DashboardService(UserService users, LikeRepository likes, CommentRepository comments, NewsQueries queries) {
        this.users = users;
        this.likes = likes;
        this.comments = comments;
        this.queries = queries;
    }

    public Dashboard summary(String userId) {
        User user = users.require(userId);
        ObjectId uid = new ObjectId(userId);
        return new Dashboard(
                true,
                new Profile(user.getName(), user.getEmail(), user.getAvatar() == null ? "" : user.getAvatar()),
                new Stats(user.getBookmarks().size(), likes.countByUser(uid), comments.countByUser(uid)));
    }

    public List<ArticleResponse> likedArticles(String userId) {
        List<ObjectId> articleIds = likes.findByUser(new ObjectId(userId)).stream()
                .map(Like::getArticle)
                .toList();
        return queries.findByIdsNewestFirst(articleIds).stream().map(ArticleResponse::from).toList();
    }
}
