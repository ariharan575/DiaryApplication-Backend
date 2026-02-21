package com.example.authapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class EmailService {

    @Value("${gmail.client-id}")
    private String clientId;

    @Value("${gmail.client-secret}")
    private String clientSecret;

    @Value("${gmail.refresh-token}")
    private String refreshToken;

    @Value("${gmail.sender-email}")
    private String senderEmail;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendOtpToEmail(String toEmail, String otp) {
        try {

            // Step 1: Get fresh access token
            String accessToken = getAccessToken();
            if (accessToken == null) {
                System.err.println("❌ Failed to get Gmail access token.");
                return;
            }

            // Step 2: Create email content
            String rawEmail =
                    "From: Diary App <" + senderEmail + ">\n" +
                            "To: " + toEmail + "\n" +
                            "Subject: OTP Verification\n" +
                            "MIME-Version: 1.0\n" +
                            "Content-Type: text/html; charset=UTF-8\n\n" +
                            "<h2>Your OTP is: " + otp + "</h2>" +
                            "<p>This OTP will expire in 5 minutes.</p>";

            // Step 3: Encode email in Base64 URL-safe format
            String encodedEmail = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(rawEmail.getBytes(StandardCharsets.UTF_8));

            // Step 4: Prepare Gmail API request
            String url = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("raw", encodedEmail);

            HttpEntity<Map<String, String>> requestEntity =
                    new HttpEntity<>(body, headers);

            // Step 5: Send email
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, requestEntity, String.class);

            System.out.println("✅ Email sent successfully to: " + toEmail);
            System.out.println("📩 Gmail API Response: " + response.getBody());

        } catch (Exception e) {
            System.err.println("❌ Error sending email via Gmail API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getAccessToken() {
        try {

            String url = "https://oauth2.googleapis.com/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // SAFE way to send form parameters
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("refresh_token", refreshToken);
            params.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> requestEntity =
                    new HttpEntity<>(params, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, requestEntity, Map.class);

            if (response.getBody() != null && response.getBody().get("access_token") != null) {
                return response.getBody().get("access_token").toString();
            } else {
                System.err.println("❌ No access_token received from Google.");
                return null;
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to get access token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}