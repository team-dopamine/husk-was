package kr.husk.application.auth.service;

import kr.husk.domain.auth.repository.AuthCodeRepository;
import kr.husk.infrastructure.config.AuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthConfig authConfig;
    private final AuthCodeRepository authCodeRepository;

    public void sendAuthCode(String key, String code) {
        // 1. 인증코드 생성
        authCodeRepository.create(key, code, authConfig.getCodeExpiration());

        // 2. 이메일 전송 (추후 구현)
    }

    public boolean verifyAuthCode(String email, String code) {
        String savedCode = authCodeRepository.read(authConfig.getKeyPrefix() + email);
        if (savedCode != null && savedCode.equals(code)) {
            authCodeRepository.delete(authConfig.getKeyPrefix() + email);
            return true;
        }
        return false;
    }

    private String generateAuthCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
