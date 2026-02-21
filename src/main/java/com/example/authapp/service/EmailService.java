package com.example.authapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class EmailService {

    @Value("${GMAIL_CLIENT_ID}")
    private String clientId;

    @Value("${GMAIL_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${GMAIL_REFRESH_TOKEN}")
    private String refreshToken;

    @Value("${GMAIL_SENDER_EMAIL}")
    private String senderEmail;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendOtpToEmail(String toEmail, String otp) {
        try {
            // Step 1: Get a fresh access token
            String accessToken = getAccessToken();
            if (accessToken == null) {
                System.err.println("Failed to get Gmail access token.");
                return;
            }

            // Step 2: Prepare raw email string
            String rawEmail = "From: " + senderEmail + "\n" +
                    "To: " + toEmail + "\n" +
                    "Subject: OTP Verification\n" +
                    "MIME-Version: 1.0\n" +
                    "Content-Type: text/html; charset=UTF-8\n\n" +
                    "<h2>Your OTP is: " + otp + "</h2>";

            // Step 3: Encode to Base64 URL-safe
            String encodedEmail = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(rawEmail.getBytes(StandardCharsets.UTF_8));

            // Step 4: Prepare Gmail API request
            String url = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("raw", encodedEmail);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            // Step 5: Send email
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("Email sent successfully via Gmail API to: " + toEmail);
            System.out.println("Gmail API response: " + response.getBody());

        } catch (Exception e) {
            System.err.println("Error sending email via Gmail API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getAccessToken() {
        try {
            String url = "https://oauth2.googleapis.com/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&refresh_token=" + refreshToken +
                    "&grant_type=refresh_token";

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            return response.getBody() != null ? (String) response.getBody().get("access_token") : null;
        } catch (Exception e) {
            System.err.println("Failed to get access token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}