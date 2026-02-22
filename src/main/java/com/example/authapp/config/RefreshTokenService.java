package com.example.authapp.config;

import com.example.authapp.exception.*;
import com.example.authapp.model.RefreshToken;
import com.example.authapp.model.User;
import com.example.authapp.repository.RefreshTokenRepository;
import com.example.authapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${refresh-token-expiration}")
    private long refreshTokenDuration;

   //        CREATE REFRESH TOKEN

    public RefreshToken createRefreshToken(User user) {

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .expiryDate(
                        Instant.now().plusMillis(refreshTokenDuration))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(token);
    }

    //        VALIDATE REFRESH TOKEN

    public User validateRefreshToken(String tokenValue) {

        RefreshToken token = refreshTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() ->
                        new ApiException(ErrorCode.REFRESH_TOKEN_INVALID,"Invalid refresh token"));

        if (token.isRevoked()) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_REUSED,"Refresh token Reused");
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_EXPIRED,"Refresh token expired");
        }

        return userRepository.findById(token.getUserId())
                .orElseThrow(() ->
                        new ApiException(ErrorCode.USER_NOT_FOUND,"User not found"));
    }

    //        REVOKE REFRESH TOKEN

    public void revoke(String tokenValue) {

        RefreshToken token = refreshTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(()-> new ApiException(ErrorCode.REFRESH_TOKEN_INVALID,"Refresh Token is Invalid!"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}

