
package com.example.authapp.config;

import com.example.authapp.exception.SecurityExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SecurityExceptionHandler securityExceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                final String token = authHeader.substring(7);

                String username = jwtService.extractUsername(token);

                List<String> roles = jwtService.extractRoles(token);

                if (jwtService.isTokenValid(token, username)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    roles.stream().map(SimpleGrantedAuthority::new).toList()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception ex) {

            
            securityExceptionHandler.commence(
                    request,
                    response,
                    new org.springframework.security.authentication.BadCredentialsException(
                            "Invalid or expired token"
                    )
            );
        }
    }
}


