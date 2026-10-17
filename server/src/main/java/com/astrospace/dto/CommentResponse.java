package com.astrospace.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * "user" and "article" are Objects on purpose: depending on the endpoint they are a populated
 * sub-document (UserSummary / {_id,title}) or a bare id, exactly like the Mongoose populate() results.
 */
public record CommentResponse(
        @JsonProperty("_id") String id,
        String text,
        Object user,
        Object article,
        Instant createdAt,
        Instant updatedAt) {
}
