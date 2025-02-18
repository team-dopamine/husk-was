package kr.husk.domain.keychain.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.keychain.dto.KeyChainDto;
import kr.husk.common.exception.GlobalException;
import kr.husk.common.jwt.util.JwtProvider;
import kr.husk.common.service.EncryptionService;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.exception.AuthExceptionCode;
import kr.husk.domain.auth.service.UserService;
import kr.husk.domain.keychain.entity.KeyChain;
import kr.husk.domain.keychain.repository.KeyChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyChainService {
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final KeyChainRepository keyChainRepository;

    public KeyChainDto.Response create(HttpServletRequest request, KeyChainDto.Request dto) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        if (!jwtProvider.validateToken(accessToken)) {
            throw new GlobalException(AuthExceptionCode.INVALID_ACCESS_TOKEN);
        }

        User user = userService.read(email);
        KeyChain keyChain = KeyChain.builder()
                .user(user)
                .name(dto.getName())
                .content(encryptionService.encrypt(dto.getContent()))
                .build();

        keyChainRepository.save(keyChain);
        log.info(email + "님의 키체인 [" + dto.getName() + "]이(가) 등록되었습니다.");
        return KeyChainDto.Response.of("키체인 등록이 완료되었습니다.");
    }

}
