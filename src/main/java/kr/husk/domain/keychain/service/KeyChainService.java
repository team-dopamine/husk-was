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
import kr.husk.domain.keychain.exception.KeyChainExceptionCode;
import kr.husk.domain.keychain.repository.KeyChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<KeyChainDto.KeyChainInfo> read(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        if (!jwtProvider.validateToken(accessToken)) {
            throw new GlobalException(AuthExceptionCode.INVALID_ACCESS_TOKEN);
        }

        User user = userService.read(email);
        return KeyChainDto.KeyChainInfo.from(user.getKeyChains());
    }

    public KeyChainDto.Response update(HttpServletRequest request, KeyChainDto.KeyChainInfo dto) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        if (!jwtProvider.validateToken(accessToken)) {
            throw new GlobalException(AuthExceptionCode.INVALID_ACCESS_TOKEN);
        }

        KeyChain keyChain = keyChainRepository.findById(dto.getId()).get();
        if (keyChain == null) {
            throw new GlobalException(KeyChainExceptionCode.KEY_CHAIN_NOT_FOUND);
        }

        keyChain.changeName(dto.getName());
        keyChain.changeContent(encryptionService.encrypt(dto.getContent()));
        keyChainRepository.save(keyChain);

        return KeyChainDto.Response.of("키체인 수정이 완료되었습니다.");
    }

    public KeyChainDto.Response delete(HttpServletRequest request, Long id) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);
        if (!jwtProvider.validateToken(accessToken)) {
            throw new GlobalException(AuthExceptionCode.INVALID_ACCESS_TOKEN);
        }

        KeyChain keyChain = keyChainRepository.findById(id).get();
        if (keyChain == null || keyChain.isDeleted()) {
            throw new GlobalException(KeyChainExceptionCode.KEY_CHAIN_NOT_FOUND);
        }

        keyChain.delete();
        keyChainRepository.save(keyChain);
        log.info("사용자 {}의 {}번 키체인이 삭제되었습니다.", email, id);

        return KeyChainDto.Response.of("키체인 삭제가 완료되었습니다.");
    }

}
