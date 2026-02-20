package com.example.authapp.repository;

import com.example.authapp.model.Otp;
import com.example.authapp.model.Otp_Usage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpRepository
        extends MongoRepository<Otp, String>
{
    Optional<Otp>  findByEmail(String email);
    Optional<Otp> findByEmailAndOtp(String email,String otp);
    Optional<Otp> findByEmailAndOtpAndUsage(String email, String otp, Otp_Usage usage);

    void deleteByEmail(String email);
}

