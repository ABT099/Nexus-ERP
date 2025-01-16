package com.nexus.auth.jwt;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

@Component
public class RsaKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RsaKeyService.class);

    private PrivateKey rsaKey;

    @PostConstruct
    private void generateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            var key = keyGen.generateKeyPair();
            this.rsaKey = key.getPrivate();
            LOGGER.info("JWT Key Generated!");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }

    public SecretKey getKey() {
        byte[] keyBytes = this.rsaKey.getEncoded();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
