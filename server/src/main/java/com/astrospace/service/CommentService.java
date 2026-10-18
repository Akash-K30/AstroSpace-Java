package com.astrospace.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.astrospace.domain.Article;
import com.astrospace.domain.Comment;
import com.astrospace.domain.User;
import com.astrospace.dto.CommentResponse;
import com.astrospace.dto.UserSummary;
import com.astrospace.repository.ArticleRepository;
import com.astrospace.repository.CommentRepository;
import com.astrospace.repository.UserRepository;
import com.astrospace.util.ApiException;
import com.astrospace.util.Ids;

@Service
public class CommentService {

    private static final int MAX_LENGTH = 2000;

    private final CommentRepository comments;
    private final ArticleRepository articles;
    private final UserRepository users;

    public CommentService(CommentRepository comments, ArticleRepository articles, UserRepository users) {
        this.comments = comments;
        this.articles = articles;
        this.users = users;
    }

    public CommentResponse add(String userId, String articleId, String text) {
        if (text == null || text.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Comment cannot be empty");
        }
        if (text.length() > MAX_LENGTH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Comment is too long");
        }
        ObjectId articleOid = Ids.require(articleId, "Article not found");
        if (!articles.existsById(articleId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Article not found");
        }

        Comment comment = new Comment();
        comment.setUser(new ObjectId(userId));
        comment.setArticle(articleOid);
        comment.setText(text);
        comment = comments.save(comment);

        UserSummary author = users.findById(userId).map(UserSummary::from).orElse(null);
        return toResponse(comment, author, articleOid.toHexString());
    }

    /** Newest first, with each author's name/avatar (populate("user", "name avatar")). */
    public List<CommentResponse> forArticle(String articleId) {
        ObjectId articleOid = Ids.parseOrNull(articleId);
        if (articleOid == null) {
            return List.of();
        }
        List<Comment> list = comments.findByArticleOrderByCreatedAtDesc(articleOid);
        Map<String, UserSummary> authors = loadAuthors(list);
        return list.stream()
                .map(c -> toResponse(c, authors.get(c.getUser().toHexString()), c.getArticle().toHexString()))
                .toList();
    }

    /** The logged-in user's own comments, each with its article's {_id, title}. */
    public List<CommentResponse> mine(String userId) {
        List<Comment> list = comments.findByUserOrderByCreatedAtDesc(new ObjectId(userId));
        Set<String> articleIds = list.stream().map(c -> c.getArticle().toHexString()).collect(Collectors.toSet());
        Map<String, Article> byId = new HashMap<>();
        articles.findAllById(articleIds).forEach(a -> byId.put(a.getId(), a));

        return list.stream().map(c -> {
            String aid = c.getArticle().toHexString();
            Article article = byId.get(aid);
            Map<String, Object> populated = new LinkedHashMap<>();
            populated.put("_id", aid);
            populated.put("title", article == null ? null : article.getTitle());
            return toResponse(c, c.getUser().toHexString(), populated);
        }).toList();
    }

    public void delete(String userId, String commentId) {
        Ids.require(commentId, "Comment not found");
        Comment comment = comments.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment not found"));
        if (!comment.getUser().toHexString().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not authorized");
        }
        comments.delete(comment);
    }

    private Map<String, UserSummary> loadAuthors(List<Comment> list) {
        Set<String> ids = list.stream().map(c -> c.getUser().toHexString()).collect(Collectors.toSet());
        Map<String, UserSummary> map = new HashMap<>();
        for (User u : users.findAllById(ids)) {
            map.put(u.getId(), UserSummary.from(u));
        }
        return map;
    }

    private static CommentResponse toResponse(Comment c, Object user, Object article) {
        return new CommentResponse(c.getId(), c.getText(), user, article, c.getCreatedAt(), c.getUpdatedAt());
    }
}
