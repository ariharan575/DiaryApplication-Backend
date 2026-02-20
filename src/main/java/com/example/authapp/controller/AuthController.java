package com.example.authapp.controller;

import com.example.authapp.config.RefreshTokenService;
import com.example.authapp.dto.authDto.*;
import com.example.authapp.exception.ApiException;
import com.example.authapp.exception.ErrorCode;
import com.example.authapp.service.AuthService;
import com.example.authapp.service.ForgetPasswordService;
import com.example.authapp.service.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private ForgetPasswordService forgetPasswordService;

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<String> register( @RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // ================= VERIFY OTP =================

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyEmailByOtp(
            @RequestBody VerifiyRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse =
                otpService.verifyOtp(
                        request.getEmail(),
                        request.getOtp(),
                        request.getUsage()
                );

        //  Store refresh token in HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(30 * 24 * 60 * 60) // 30 days
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        //  Return only access token to frontend
        return ResponseEntity.ok(
                new AuthResponse(
                        authResponse.getAccessToken(),
                        null,
                        authResponse.getUsername(),
                        authResponse.getUserId(),
                        authResponse.getRoles()
                )
        );
    }
    // ================= FORGET-PASSWORD =================

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordRequest request){
        String response = forgetPasswordService.forgetpassword(request.getEmail());
        return ResponseEntity.ok(response);
    }

    // ================= RESET-PASSWORD================

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody AuthRequest request){
        String response = forgetPasswordService.resetPassword(request.getEmail(),request.getPassword());
        return ResponseEntity.ok(response);
    }

    // ================= REFRESH =================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_INVALID, "Refresh token missing");
        }

        String accessToken = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }


    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.login(request);

        // Store refresh token in HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                new AuthResponse(
                        authResponse.getAccessToken(),
                        null,
                        authResponse.getUsername(),
                        authResponse.getUserId(),
                        authResponse.getRoles()
                )
        );
    }


    // ================= LOGOUT =================

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null) {
            refreshTokenService.revoke(refreshToken);
        }

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

}


