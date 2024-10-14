package kr.husk.domain.auth.service;

import kr.husk.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean isExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
