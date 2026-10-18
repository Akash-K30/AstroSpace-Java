package com.astrospace.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.astrospace.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
