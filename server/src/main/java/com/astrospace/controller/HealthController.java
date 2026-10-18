package com.astrospace.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astrospace.dto.Responses.Message;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Message health() {
        return Message.ok("Server is healthy");
    }
}
