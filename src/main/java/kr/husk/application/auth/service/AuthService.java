package kr.husk.application.auth.service;

import kr.husk.domain.auth.repository.AuthCodeRepository;
import kr.husk.infrastructure.config.AuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthConfig authConfig;
    private final JavaMailSender mailSender;
    private final AuthCodeRepository authCodeRepository;

    public void sendAuthCode(String to) {
        // 1. 인증코드 생성
        String authCode = generateAuthCode();
        authCodeRepository.create(to, authCode, authConfig.getCodeExpiration());

        // 2. 이메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[HUSK] 회원가입을 위한 인증코드 발송");
        message.setText("인증코드: " + authCode);
        mailSender.send(message);
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
