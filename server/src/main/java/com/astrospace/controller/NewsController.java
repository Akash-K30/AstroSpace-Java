package com.astrospace.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.astrospace.dto.ArticleResponse;
import com.astrospace.dto.Responses.Message;
import com.astrospace.service.NewsService;
import com.astrospace.service.NewsSyncService;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService news;
    private final NewsSyncService sync;

    public NewsController(NewsService news, NewsSyncService sync) {
        this.news = news;
        this.sync = sync;
    }

    /** Public; when the request carries a valid cookie each article also gets isLiked / isBookmarked. */
    @GetMapping({"", "/"})
    public List<ArticleResponse> list(Principal principal,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) String q,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int limit) {
        String userId = principal == null ? null : principal.getName();
        return news.list(userId, category, q, sort, page, limit);
    }

    @GetMapping("/search")
    public List<ArticleResponse> search(@RequestParam(required = false) String q) {
        return news.search(q);
    }

    /** Unauthenticated like in the Node version; protected by the 2-per-minute rate limit. */
    @PostMapping("/sync")
    public Message sync() {
        sync.sync();
        return Message.ok("Sync complete");
    }
}
