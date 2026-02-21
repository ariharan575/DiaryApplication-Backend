package com.example.authapp.service;

import com.example.authapp.config.JwtService;
import com.example.authapp.config.RefreshTokenService;
import com.example.authapp.dto.authDto.AuthRequest;
import com.example.authapp.dto.authDto.AuthResponse;
import com.example.authapp.dto.authDto.RegisterRequest;
import com.example.authapp.exception.ApiException;
import com.example.authapp.exception.ErrorCode;
import com.example.authapp.model.Otp_Usage;
import com.example.authapp.model.RefreshToken;
import com.example.authapp.model.User;
import com.example.authapp.repository.UserRepository;
import com.example.authapp.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS,"Email already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .enabled(true)
                .roles(List.of(Constants.ROLE_USER))
                .build();

         String otp =  otpService.generateOtp();

        try {
            emailService.sendOtpToEmail(request.getEmail(), otp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.MAIL_FAILED,e.getMessage());
        }

         otpService.saveOrUpdateOtp(request.getEmail(),otp,Otp_Usage.REGISTER,user);

        return "OTP send Your Email";

    }

    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS,"Invalid credentials"));

        if(!user.isEmailVerified()){
            throw new ApiException(ErrorCode.EMAIL_NOT_VERIFIED,"Email is not Verified");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS,"Invalid Email or Password");
        }

        String accessToken = jwtService.generateToken(user.getEmail(), user);
        RefreshToken refreshToken =refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken,refreshToken.getToken(), user.getUsername(), user.getId(),user.getRoles());
    }

    public String refreshAccessToken(String refreshToken) {

        User user = refreshTokenService.validateRefreshToken(refreshToken);

        return jwtService.generateToken(
                user.getEmail(), user
        );
    }
}
