package com.example.authapp.dto.authDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordRequest {


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
