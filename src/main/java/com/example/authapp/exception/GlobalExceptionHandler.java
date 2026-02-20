package com.example.authapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== VALIDATION =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(e -> e.getDefaultMessage())
                .orElse("Invalid input");

        return build(HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                msg);
    }

    // ===== BUSINESS =====
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        return build(HttpStatus.BAD_REQUEST,
                ex.getCode(),
                ex.getMessage());
    }

    // ===== FALLBACK =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {

        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_ERROR,
                "Something went wrong. Please try again.");
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            ErrorCode code,
            String message) {

        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .timestamp(java.time.Instant.now().toString())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .code(code)
                        .message(message)
                        .build()
        );
    }
}
