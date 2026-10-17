package com.astrospace.dto;

import com.astrospace.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/** populate("user", "name avatar") */
public record UserSummary(@JsonProperty("_id") String id, String name, String avatar) {

    public static UserSummary from(User u) {
        return new UserSummary(u.getId(), u.getName(), u.getAvatar());
    }
}
