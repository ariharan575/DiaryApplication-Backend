package com.example.authapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendOtpToEmail(String toEmail, String otp) {

        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("from", "onboarding@resend.dev"); // Free tier only
        body.put("to", toEmail);
        body.put("subject", "OTP Verification");
        body.put("html", "<h2>Your OTP is: " + otp + "</h2>");

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            System.out.println("Email sent successfully: " + response.getBody());

        } catch (HttpClientErrorException e) {
            System.err.println("Resend API Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
        }
    }
}