package com.example.authapp.dto.authDto;

import com.example.authapp.model.Otp_Usage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifiyRequest {

    @NotBlank(message = "OTP is required")
    @Size(min = 8, message = "otp must be 6 characters")
    private String otp;

    private Otp_Usage usage;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
