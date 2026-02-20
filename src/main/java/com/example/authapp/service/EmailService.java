package com.example.authapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpToEmail(String toEmail,String otp){
        SimpleMailMessage sendOtp = new SimpleMailMessage();
        sendOtp.setFrom("notesdaily19@gmail.com");
        sendOtp.setTo(toEmail);
        sendOtp.setSubject("OTP verification for Register");
        sendOtp.setText("Hi,       " +
                "Enter this code to continue logging in , code is valid for 5 minites ." +
                " By entering this code, you will also confirm the email address associated with your account."  + "OTP: " + otp);

        javaMailSender.send(sendOtp);
    }


}

