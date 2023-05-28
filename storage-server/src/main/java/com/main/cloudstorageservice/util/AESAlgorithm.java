package com.main.cloudstorageservice.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
@Component
public class AESAlgorithm {

    private static final String AES_ALGORITHM = "AES";
    private static final String ENCRYPTION_MODE = "AES/ECB/PKCS5Padding";

    public static byte[] encrypt(byte[] plaintext, String secretKey) throws Exception {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        return cipher.doFinal(plaintext);
    }

    public static byte[] decrypt(byte[] ciphertext, String secretKey) throws Exception {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        return cipher.doFinal(ciphertext);
    }
}
