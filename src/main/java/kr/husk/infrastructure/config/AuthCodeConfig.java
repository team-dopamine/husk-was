package kr.husk.infrastructure.config;

import kr.husk.domain.repository.AuthCodeRepository;
import kr.husk.infrastructure.persistence.ConcurrentMapAuthCodeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthCodeConfig {
    @Bean
    public AuthCodeRepository authCodeRepository() {
        return new ConcurrentMapAuthCodeRepository();
    }
}
