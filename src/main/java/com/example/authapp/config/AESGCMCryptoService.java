package com.example.authapp.config;

import com.example.authapp.exception.ApiException;
import com.example.authapp.exception.ErrorCode;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AESGCMCryptoService {

    private static final String AES = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;   
    private static final int TAG_LENGTH = 128; 

    private final SecureRandom secureRandom = new SecureRandom();

    // ================= ENCRYPT =================

    public String encrypt(String plainText, String base64Key, String aad) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey key = decodeKey(base64Key);

            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            cipher.updateAAD(aad.getBytes(StandardCharsets.UTF_8));

            byte[] cipherText = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8)
            );

            byte[] encrypted = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    ErrorCode.CRYPTO_INVALID_KEY,
                    "Invalid encryption key"
            );
        } catch (Exception e) {
            throw new ApiException(
                    ErrorCode.CRYPTO_ENCRYPT_FAILED,
                    "Encryption failed"
            );
        }
    }

    // ================= DECRYPT =================

    public String decrypt(String encryptedText, String base64Key, String aad) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            byte[] iv = new byte[IV_LENGTH];
            byte[] cipherText = new byte[decoded.length - IV_LENGTH];

            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
            System.arraycopy(decoded, IV_LENGTH, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey key = decodeKey(base64Key);

            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(aad.getBytes(StandardCharsets.UTF_8));

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);

        } catch (javax.crypto.AEADBadTagException e) {
            throw new ApiException(
                    ErrorCode.CRYPTO_DATA_TAMPERED,
                    "Encrypted data is invalid"
            );
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    ErrorCode.CRYPTO_INVALID_KEY,
                    "Invalid decryption key"
            );
        } catch (Exception e) {
            throw new ApiException(
                    ErrorCode.CRYPTO_DECRYPT_FAILED,
                    "Decryption failed"
            );
        }
    }

    // ================= KEY =================

    private SecretKey decodeKey(String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);

        if (keyBytes.length != 16 && keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "Invalid AES key length: " + keyBytes.length
            );
        }

        return new SecretKeySpec(keyBytes, AES);
    }
}
