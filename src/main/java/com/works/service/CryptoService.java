package com.works.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CryptoService {

    @Value("${security.crypto.aes-key}")
    private String aesKey;

    @Value("${security.crypto.hmac-key}")
    private String hmacKey;

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    public String encrypt(String value) {
        try {

            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key =
                    new SecretKeySpec(
                            Base64.getDecoder().decode(aesKey),
                            "AES"
                    );

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] encrypted =
                    cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer =
                    ByteBuffer.allocate(iv.length + encrypted.length);

            buffer.put(iv);
            buffer.put(encrypted);

            return Base64.getEncoder()
                    .encodeToString(buffer.array());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public String decrypt(String encryptedValue) {

        try {

            byte[] data =
                    Base64.getDecoder().decode(encryptedValue);

            ByteBuffer buffer = ByteBuffer.wrap(data);

            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);

            byte[] cipherText =
                    new byte[buffer.remaining()];

            buffer.get(cipherText);

            Cipher cipher =
                    Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key =
                    new SecretKeySpec(
                            Base64.getDecoder().decode(aesKey),
                            "AES"
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] decrypted =
                    cipher.doFinal(cipherText);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public String hmac(String value) {

        try {

            Mac mac = Mac.getInstance("HmacSHA256");

            mac.init(
                    new SecretKeySpec(
                            hmacKey.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    )
            );

            byte[] hash =
                    mac.doFinal(
                            value.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
