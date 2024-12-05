package com.nexus.auth.jwt;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

@Component
public class RsaKeyService {

    private PrivateKey rsaKey;

    @PostConstruct
    private void generateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            var key = keyGen.generateKeyPair();
            this.rsaKey = key.getPrivate();
            System.out.println("jwt key generated!");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }

    public SecretKey getKey() {
        byte[] keyBytes = this.rsaKey.getEncoded();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
