package com.astrospace.dto;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;

import com.astrospace.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A user without the password hash (what Express returned from /auth/me via select("-password")). */
public record UserResponse(
        @JsonProperty("_id") String id,
        String name,
        String email,
        String avatar,
        List<String> bookmarks,
        String role,
        Instant createdAt,
        Instant updatedAt) {

    public static UserResponse from(User u) {
        List<String> bookmarks = u.getBookmarks() == null
                ? List.of()
                : u.getBookmarks().stream().map(ObjectId::toHexString).toList();
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getAvatar(), bookmarks, u.getRole(),
                u.getCreatedAt(), u.getUpdatedAt());
    }
}
