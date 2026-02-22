package com.example.authapp.repository;

import com.example.authapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmailVerifiedFalseAndOtpExpiryTimeBefore(LocalDateTime time);
}

