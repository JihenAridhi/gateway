package com.uib.gateway.Services;

import java.sql.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.uib.gateway.Entities.User;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user) {
        return JWT.create()
            .withSubject(user.getUsername())
            .withClaim("id", user.getId())
            .withClaim("email", user.getEmail())
            .withArrayClaim("authorities", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new))
            .withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000))
            .sign(Algorithm.HMAC256(secret));
    }

    public String extractPayload(String token) {
        return JWT.decode(token).getClaim("email").toString();
    }
}