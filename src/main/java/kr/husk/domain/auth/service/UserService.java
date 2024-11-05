package kr.husk.domain.auth.service;

import kr.husk.domain.auth.entity.User;
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

    public User create(String email, String password) {
        return userRepository.save(User.builder()
                .email(email)
                .password(password)
                .oAuthProvider(OAuthProvider.NONE)
                .build());
    }

    public boolean isExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
