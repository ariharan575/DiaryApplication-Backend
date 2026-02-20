package com.example.authapp.service;

import com.example.authapp.config.AESGCMCryptoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class DiaryEncryptionService {

    private final AESGCMCryptoService cryptoService;

    @Value("${security.encryption.aes-key}")
    private String masterKey;

    public DiaryEncryptionService(AESGCMCryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostConstruct
    public void validateKey() {
        if (masterKey == null || masterKey.isBlank()) {
            throw new IllegalStateException(
                    "AES KEY MISSING! Check security.encryption.aes-key"
            );
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(masterKey);
            if (decoded.length != 16 && decoded.length != 32) {
                throw new IllegalStateException(
                        "AES KEY MUST be 16 or 32 bytes after Base64 decode"
                );
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "AES KEY is NOT valid Base64", e
            );
        }
    }

    public String encryptDiary(String plainText, String userId) {
        return cryptoService.encrypt(
                plainText,
                masterKey,
                userId
        );
    }

    public String decryptDiary(String encryptedText, String userId) {
        return cryptoService.decrypt(
                encryptedText,
                masterKey,
                userId
        );
    }
}
