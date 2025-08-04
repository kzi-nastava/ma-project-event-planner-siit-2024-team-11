package com.example.eventy.common;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final byte[] SECRET_KEY = "MySuperSecretKey".getBytes(); // Replace with a secure key

    public static String encrypt(String data, long expirationTime) throws Exception {
        String dataWithExpiration = data + "|" + expirationTime; // Add expiration timestamp to the data
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encryptedBytes = cipher.doFinal(dataWithExpiration.getBytes());
        return Base64.getUrlEncoder().encodeToString(encryptedBytes); // URL-safe encoding
    }

    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        String decrypted = new String(decryptedBytes);
        String[] parts = decrypted.split("\\|");
        String email = parts[0];
        long expirationTime = Long.parseLong(parts[1]);

        // Check expiration time
        if (System.currentTimeMillis() > expirationTime) {
            throw new RuntimeException("Link has expired");
        }

        return email;
    }
}
