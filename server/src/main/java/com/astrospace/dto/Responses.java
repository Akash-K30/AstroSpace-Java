package com.astrospace.dto;

import java.util.List;

public final class Responses {

    private Responses() {
    }

    public record Message(boolean success, String message) {
        public static Message ok(String message) {
            return new Message(true, message);
        }
    }

    public record Bookmarks(boolean success, List<ArticleResponse> bookmarks) {
    }

    public record Articles(boolean success, List<ArticleResponse> articles) {
    }

    public record Comments(boolean success, List<CommentResponse> comments) {
    }

    public record CommentCreated(boolean success, CommentResponse comment) {
    }

    public record ProfileUpdated(boolean success, UserResponse user) {
    }

    public record LikeResult(boolean liked, long likeCount) {
    }


    public record AuthUser(String id, String name, String email) {
    }

    public record Auth(AuthUser user) {
    }

    public record Profile(String name, String email, String avatar) {
    }

    public record Stats(int bookmarks, long likes, long comments) {
    }

    public record Dashboard(boolean success, Profile profile, Stats stats) {
    }
}
