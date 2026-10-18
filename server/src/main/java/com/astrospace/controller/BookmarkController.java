package com.astrospace.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astrospace.dto.Responses.Bookmarks;
import com.astrospace.dto.Responses.Message;
import com.astrospace.service.BookmarkService;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarks;

    public BookmarkController(BookmarkService bookmarks) {
        this.bookmarks = bookmarks;
    }

    @PostMapping("/{articleId}")
    public Message add(Principal principal, @PathVariable String articleId) {
        bookmarks.add(principal.getName(), articleId);
        return Message.ok("Bookmark added");
    }

    @DeleteMapping("/{articleId}")
    public Message remove(Principal principal, @PathVariable String articleId) {
        bookmarks.remove(principal.getName(), articleId);
        return Message.ok("Bookmark removed");
    }

    @GetMapping
    public Bookmarks list(Principal principal) {
        return new Bookmarks(true, bookmarks.list(principal.getName()));
    }
}
