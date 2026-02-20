package com.example.authapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "otp")
public class Otp {

    @Id
    private String id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "OTP is Empty")
    private String otp;

    private boolean verified= false;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Otp_Usage usage;

    private LocalDateTime expiryTime;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

}
