package com.astrospace.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astrospace.dto.Requests;
import com.astrospace.dto.Responses.CommentCreated;
import com.astrospace.dto.Responses.Comments;
import com.astrospace.dto.Responses.Message;
import com.astrospace.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService comments;

    public CommentController(CommentService comments) {
        this.comments = comments;
    }

    @PostMapping("/{articleId}")
    public ResponseEntity<CommentCreated> add(Principal principal,
                                              @PathVariable String articleId,
                                              @RequestBody Requests.AddComment body) {
        CommentCreated created = new CommentCreated(true,
                comments.add(principal.getName(), articleId, body.text()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Public. */
    @GetMapping("/{articleId}")
    public Comments list(@PathVariable String articleId) {
        return new Comments(true, comments.forArticle(articleId));
    }

    @DeleteMapping("/{commentId}")
    public Message delete(Principal principal, @PathVariable String commentId) {
        comments.delete(principal.getName(), commentId);
        return Message.ok("Comment deleted");
    }
}
