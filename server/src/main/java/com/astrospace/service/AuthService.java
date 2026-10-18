package com.astrospace.service;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.astrospace.domain.User;
import com.astrospace.repository.UserRepository;
import com.astrospace.security.JwtService;
import com.astrospace.util.ApiException;

@Service
public class AuthService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int BCRYPT_MAX_BYTES = 72;

    public record AuthResult(User user, String token) {
    }

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResult register(String name, String email, String password) {
        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "All fields required");
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (!EMAIL.matcher(normalizedEmail).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid email");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password is too long");
        }
        if (users.existsByEmail(normalizedEmail)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(encoder.encode(password));
        try {
            user = users.save(user);
        } catch (DuplicateKeyException e) { // lost a race against the unique email index
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        return new AuthResult(user, jwt.generate(user.getId()));
    }

    public AuthResult login(String email, String password) {
        ApiException invalid = new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        if (isBlank(email) || isBlank(password)) {
            throw invalid;
        }
        User user = users.findByEmail(email.trim().toLowerCase()).orElseThrow(() -> invalid);
        if (!encoder.matches(password, user.getPassword())) {
            throw invalid;
        }
        return new AuthResult(user, jwt.generate(user.getId()));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
