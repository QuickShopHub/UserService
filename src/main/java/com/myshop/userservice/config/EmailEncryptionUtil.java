package com.myshop.userservice.config;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EmailEncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "12345678901234567890123456789012"; // Должен быть 16, 24 или 32 байта

    public static String encodeEmail(String email) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedEmail = cipher.doFinal(email.getBytes());
        return Base64.getEncoder().encodeToString(encryptedEmail);
    }

    public static String decodeEmail(String encryptedEmail) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedEmail = cipher.doFinal(Base64.getDecoder().decode(encryptedEmail));
        return new String(decryptedEmail);
    }
}