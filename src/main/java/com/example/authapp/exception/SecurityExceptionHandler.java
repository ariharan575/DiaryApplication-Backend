package com.example.authapp.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class SecurityExceptionHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex
    ) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex
    ) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        write(response,
                HttpStatus.FORBIDDEN,
                ErrorCode.ACCESS_DENIED,
                "You do not have permission");
    }

    private void write(
            HttpServletResponse response,
            HttpStatus status,
            ErrorCode code,
            String message
    ) throws IOException {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", code.name());
        body.put("message", message);

        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
