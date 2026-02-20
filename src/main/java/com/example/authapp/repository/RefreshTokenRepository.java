package com.example.authapp.repository;

import com.example.authapp.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(String userId);
}
