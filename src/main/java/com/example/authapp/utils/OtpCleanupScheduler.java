package com.example.authapp.utils;

import com.example.authapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class OtpCleanupScheduler {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredUnverifiedUsers(){
        userRepository.deleteByEmailVerifiedFalseAndOtpExpiryTimeBefore(LocalDateTime.now());
    }
}
