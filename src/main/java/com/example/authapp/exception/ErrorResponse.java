package com.example.authapp.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private ErrorCode code;
    private String message;
}
