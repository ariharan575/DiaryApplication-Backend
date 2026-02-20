package com.example.authapp.config;

import com.example.authapp.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${access-token-expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    //        GENERATE JWT

    public String generateToken(String username, User user) {

        return Jwts.builder()
                .subject(username)
                .claim("roles", user.getRoles())
                .claim("userId",user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    //     EXTRACT USERNAME (sub)

    public String extractUsername(String token) {
        return
                extractAllClaims(token).getSubject();
    }

    //       EXTRACT ROLES

    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    //       VALIDATE TOKEN

    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) &&
                !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    //       NEW PARSER 

    private Claims extractAllClaims(String token) {
        Jws<Claims> claimsJws = Jwts.parser()      
                .verifyWith(getSigningKey())      
                .build()
                .parseSignedClaims(token);         

        return claimsJws.getPayload();
    }
}



