package com.astrospace.controller;

import java.security.Principal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.astrospace.domain.User;
import com.astrospace.dto.Requests;
import com.astrospace.dto.Responses.Auth;
import com.astrospace.dto.Responses.AuthUser;
import com.astrospace.dto.Responses.Message;
import com.astrospace.dto.UserResponse;
import com.astrospace.security.AuthCookies;
import com.astrospace.service.AuthService;
import com.astrospace.service.AuthService.AuthResult;
import com.astrospace.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;
    private final UserService users;
    private final AuthCookies cookies;

    public AuthController(AuthService auth, UserService users, AuthCookies cookies) {
        this.auth = auth;
        this.users = users;
        this.cookies = cookies;
    }

    @PostMapping("/register")
    public ResponseEntity<Auth> register(@RequestBody Requests.Register body) {
        AuthResult result = auth.register(body.name(), body.email(), body.password());
        return withSession(HttpStatus.CREATED, result);
    }

    @PostMapping("/login")
    public ResponseEntity<Auth> login(@RequestBody Requests.Login body) {
        AuthResult result = auth.login(body.email(), body.password());
        return withSession(HttpStatus.OK, result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.clear().toString())
                .body(Message.ok("Logged out"));
    }

    @GetMapping("/me")
    public UserResponse me(Principal principal) {
        return users.profile(principal.getName());
    }

    private ResponseEntity<Auth> withSession(HttpStatus status, AuthResult result) {
        User u = result.user();
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, cookies.issue(result.token()).toString())
                .body(new Auth(new AuthUser(u.getId(), u.getName(), u.getEmail())));
    }
}
