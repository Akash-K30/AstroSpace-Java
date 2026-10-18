package com.astrospace.service;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.astrospace.domain.User;
import com.astrospace.dto.UserResponse;
import com.astrospace.repository.UserRepository;
import com.astrospace.util.ApiException;
import com.astrospace.util.Ids;

import java.time.Instant;

@Service
public class UserService {

    private static final int MAX_NAME = 100;
    private static final int MAX_AVATAR = 2048;

    private final UserRepository users;
    private final MongoTemplate mongo;

    public UserService(UserRepository users, MongoTemplate mongo) {
        this.users = users;
        this.mongo = mongo;
    }

    public User require(String userId) {
        return users.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    public UserResponse profile(String userId) {
        return UserResponse.from(require(userId));
    }

    /**
     * Targeted $set (instead of load + save) so fields this app doesn't map are never wiped
     * from the existing documents.
     */
    public UserResponse updateProfile(String userId, String name, String avatar) {
        ObjectId id = Ids.require(userId, "User not found");
        Update update = new Update();
        boolean changed = false;

        if (name != null && !name.isBlank()) {
            if (name.trim().length() > MAX_NAME) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Name is too long");
            }
            update.set("name", name.trim());
            changed = true;
        }
        if (avatar != null && !avatar.isBlank()) {
            if (avatar.length() > MAX_AVATAR) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Avatar URL is too long");
            }
            update.set("avatar", avatar);
            changed = true;
        }
        if (!changed) {
            return profile(userId);
        }
        update.set("updatedAt", Instant.now());

        User updated = mongo.findAndModify(
                Query.query(Criteria.where("_id").is(id)),
                update,
                FindAndModifyOptions.options().returnNew(true),
                User.class);
        if (updated == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        return UserResponse.from(updated);
    }
}
