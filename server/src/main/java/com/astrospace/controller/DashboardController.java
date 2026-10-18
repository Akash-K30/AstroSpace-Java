package com.astrospace.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astrospace.dto.Requests;
import com.astrospace.dto.Responses.Articles;
import com.astrospace.dto.Responses.Comments;
import com.astrospace.dto.Responses.Dashboard;
import com.astrospace.dto.Responses.ProfileUpdated;
import com.astrospace.service.CommentService;
import com.astrospace.service.DashboardService;
import com.astrospace.service.UserService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboard;
    private final CommentService comments;
    private final UserService users;

    public DashboardController(DashboardService dashboard, CommentService comments, UserService users) {
        this.dashboard = dashboard;
        this.comments = comments;
        this.users = users;
    }

    @GetMapping
    public Dashboard summary(Principal principal) {
        return dashboard.summary(principal.getName());
    }

    @PatchMapping("/profile")
    public ProfileUpdated updateProfile(Principal principal, @RequestBody Requests.UpdateProfile body) {
        return new ProfileUpdated(true, users.updateProfile(principal.getName(), body.name(), body.avatar()));
    }

    @GetMapping("/likes")
    public Articles likes(Principal principal) {
        return new Articles(true, dashboard.likedArticles(principal.getName()));
    }

    @GetMapping("/comments")
    public Comments myComments(Principal principal) {
        return new Comments(true, comments.mine(principal.getName()));
    }
}
