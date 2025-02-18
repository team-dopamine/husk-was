package kr.husk.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;

@Configuration
public class EncryptionConfig {

    @Value("${encryption.salt}")
    private String salt;
    @Value("${encryption.password}")
    private String password;

    @Bean
    public BytesEncryptor bytesEncryptor() {
        return Encryptors.stronger(password, salt);
    }
}
