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
        return userRepository.findByEmailAndOAuthProvider(email, oAuthProvider)
                .orElseThrow(() -> new GlobalException(UserExceptionCode.EMAIL_IS_NOT_FOUND));
    }

    public User read(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void update(User user, String newPassowrd) {
        user.updatePassword(newPassowrd);
    }

    public boolean isExist(String email, OAuthProvider oAuthProvider) {
        return userRepository.findByEmailAndOAuthProvider(email, oAuthProvider).isPresent();
    }
}