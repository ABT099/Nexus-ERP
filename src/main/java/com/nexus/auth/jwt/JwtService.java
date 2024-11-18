package com.nexus.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final RsaKeyService rsaKeyService;

    public JwtService(RsaKeyService rsaKeyService) {
        this.rsaKeyService = rsaKeyService;
    }

    public String generateToken(String username, String userId) {
        return Jwts.builder()
                .subject(username)
                .id(userId)
                .signWith(rsaKeyService.getKey())
                .compact();
    }

    public boolean validate(String token, UserDetails userDetails) {
        final String username = extractClaim(token, Claims::getSubject);
        return username.equals(userDetails.getUsername());
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(rsaKeyService.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolver.apply(claims);
    }
}
