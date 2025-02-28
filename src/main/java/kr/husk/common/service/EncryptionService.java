package kr.husk.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptionService {

    private final BytesEncryptor bytesEncryptor;

    public String encrypt(String value) {
        byte[] contentBytes = value.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = bytesEncryptor.encrypt(contentBytes);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedValue) {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedBytes = bytesEncryptor.decrypt(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
