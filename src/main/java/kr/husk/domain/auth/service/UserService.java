package kr.husk.domain.auth.service;

import kr.husk.common.exception.GlobalException;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.exception.UserExceptionCode;
import kr.husk.domain.auth.repository.UserRepository;
import kr.husk.domain.auth.type.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public User read(String email, OAuthProvider oAuthProvider) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getOAuthProvider().equals(oAuthProvider))
                .orElseThrow(() -> new GlobalException(UserExceptionCode.EMAIL_IS_NOT_FOUND));
    }

    public boolean isExist(String email, OAuthProvider oAuthProvider) {
        return userRepository.findByEmail(email)
                .map(user -> user.getOAuthProvider().equals(oAuthProvider))
                .orElse(false);
    }
}