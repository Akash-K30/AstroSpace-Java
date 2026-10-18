package com.astrospace.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astrospace.dto.Responses.LikeResult;
import com.astrospace.service.LikeService;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likes;

    public LikeController(LikeService likes) {
        this.likes = likes;
    }

    @PostMapping("/{articleId}")
    public LikeResult toggle(Principal principal, @PathVariable String articleId) {
        return likes.toggle(principal.getName(), articleId);
    }
}
