package com.example.authapp.service;

import com.example.authapp.exception.ApiException;
import com.example.authapp.exception.ErrorCode;
import com.example.authapp.model.Otp;
import com.example.authapp.model.Otp_Usage;
import com.example.authapp.model.User;
import com.example.authapp.repository.OtpRepository;
import com.example.authapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ForgetPasswordService {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String forgetpassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND,"User not found"));

        String otp = otpService.generateOtp();

        try {
            emailService.sendOtpToEmail(email, otp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.MAIL_FAILED,e.getMessage());
        }
        otpService.saveOrUpdateOtp(user.getEmail(),otp,Otp_Usage.FORGET_PASSWORD,user);

        return "OTP sent to registered email";
    }

    public String resetPassword(String email, String password) {
        Otp otpEntity =otpRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.OTP_INVALID,"OTP is Invalid"));

        if(!otpEntity.isVerified()){
            throw new ApiException(ErrorCode.OTP_NOT_VERIFIED,"Otp not Verified");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(ErrorCode.USER_NOT_FOUND,"User not found"));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        otpRepository.deleteByEmail(email);
        return "Password has been Changed Successfully!";
    }
}
