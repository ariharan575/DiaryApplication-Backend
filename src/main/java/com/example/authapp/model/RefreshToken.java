package com.example.authapp.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String id;

    private String token;

    @NotNull(message = "userId is Empty")
    private String userId;

    private Instant expiryDate = Instant.now().plusMillis(5);

    private boolean revoked = false;

    private boolean used;
}
