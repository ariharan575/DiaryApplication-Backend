package com.example.authapp.service;

import com.example.authapp.config.JwtService;
import com.example.authapp.config.RefreshTokenService;
import com.example.authapp.dto.authDto.AuthResponse;
import com.example.authapp.exception.ApiException;
import com.example.authapp.exception.ErrorCode;
import com.example.authapp.model.Otp;
import com.example.authapp.model.Otp_Usage;
import com.example.authapp.model.RefreshToken;
import com.example.authapp.model.User;
import com.example.authapp.repository.OtpRepository;
import com.example.authapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private static final SecureRandom random = new SecureRandom();

    public String generateOtp(){
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public void saveOrUpdateOtp(String email,String otp,Otp_Usage usage,User user){

        Otp_Usage otpUsage = (usage != null) ? usage : Otp_Usage.REGISTER;

        Optional<Otp> existingOtp = otpRepository.findByEmail(email);

        Otp otpVerification;

        if(existingOtp.isPresent()){

            otpVerification = existingOtp.get();
            otpVerification.setOtp(otp);
            otpVerification.setVerified(false);
            otpVerification.setExpiryTime(LocalDateTime.now().plusMinutes(5));
            otpVerification.setUsage(otpUsage);
        }else {

            otpVerification = Otp.builder()
                    .email(email)
                    .otp(otp)
                    .verified(false)
                    .usage(otpUsage)
                    .expiryTime(LocalDateTime.now().plusMinutes(5)).build();

        }

        otpRepository.save(otpVerification);

        userRepository.save(user);
    }

    public AuthResponse verifyOtp(String email, String otp, Otp_Usage usage) {

        Otp_Usage otpUsage = (usage != null) ? usage : Otp_Usage.REGISTER;

        Otp otpVerification = otpRepository.findByEmailAndOtpAndUsage(email,otp,otpUsage)
                .orElseThrow(()-> new ApiException(ErrorCode.OTP_INVALID,"Invalid OTP"));

        if(otpVerification.isExpired()){
            throw new ApiException(ErrorCode.OTP_EXPIRED,"OTP expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND,"User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);

        otpVerification.setOtp(null);
        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);

        String accessToken = jwtService.generateToken(user.getEmail(), user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);


        return new AuthResponse(accessToken,refreshToken.getToken(),
                user.getUsername(), user.getId(), user.getRoles()
        );
    }
}

