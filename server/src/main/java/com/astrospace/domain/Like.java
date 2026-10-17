package com.astrospace.domain;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "likes")
public class Like {

    @Id
    private String id;
    private ObjectId user;
    private ObjectId article;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public Like() {
    }

    public Like(ObjectId user, ObjectId article) {
        this.user = user;
        this.article = article;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ObjectId getUser() { return user; }
    public void setUser(ObjectId user) { this.user = user; }
    public ObjectId getArticle() { return article; }
    public void setArticle(ObjectId article) { this.article = article; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
