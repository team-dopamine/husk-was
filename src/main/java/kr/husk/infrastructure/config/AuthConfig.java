package kr.husk.infrastructure.config;

import kr.husk.domain.auth.repository.AuthCodeRepository;
import kr.husk.infrastructure.persistence.ConcurrentMapAuthCodeRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "husk.auth")
@Getter
@Setter
public class AuthConfig {
    private long codeExpiration;
    private String keyPrefix;

    @Bean
    public AuthCodeRepository authCodeRepository() {
        return new ConcurrentMapAuthCodeRepository();
    }
}
