package kr.husk.domain.keychain.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.keychain.dto.KeyChainDto;
import kr.husk.common.exception.GlobalException;
import kr.husk.common.jwt.util.JwtProvider;
import kr.husk.common.service.EncryptionService;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.service.UserService;
import kr.husk.domain.keychain.entity.KeyChain;
import kr.husk.domain.keychain.exception.KeyChainExceptionCode;
import kr.husk.domain.keychain.repository.KeyChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyChainService {
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final KeyChainRepository keyChainRepository;

    @Transactional
    public KeyChainDto.Response create(HttpServletRequest request, KeyChainDto.Request dto) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

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

    @Transactional
    public List<KeyChainDto.Overview> read(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        User user = userService.read(email);
        return KeyChainDto.Overview.from(user.getKeyChains());
    }

    @Transactional
    public KeyChain read(User user, String name) {
        List<KeyChain> keyChainList = user.getKeyChains();
        for (KeyChain keyChain : keyChainList) {
            if (keyChain.getName().equals(name) && !keyChain.isDeleted()) {
                return keyChain;
            }
        }
        return null;
    }

    @Transactional
    public KeyChainDto.Response update(HttpServletRequest request, KeyChainDto.UpdateRequest dto) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        KeyChain keyChain = keyChainRepository.findById(dto.getId()).get();
        if (!isAccessible(keyChain, email)) {
            throw new GlobalException(KeyChainExceptionCode.KEY_CHAIN_NOT_FOUND);
        }

        keyChain.changeName(dto.getName());
        keyChain.changeContent(encryptionService.encrypt(dto.getContent()));
        keyChainRepository.save(keyChain);

        return KeyChainDto.Response.of("키체인 수정이 완료되었습니다.");
    }

    @Transactional
    public KeyChainDto.Response delete(HttpServletRequest request, Long id) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        KeyChain keyChain = keyChainRepository.findById(id).get();
        if (!isAccessible(keyChain, email)) {
            throw new GlobalException(KeyChainExceptionCode.KEY_CHAIN_NOT_FOUND);
        }

        keyChain.delete();
        keyChainRepository.save(keyChain);
        log.info("사용자 {}의 {}번 키체인이 삭제되었습니다.", email, id);

        return KeyChainDto.Response.of("키체인 삭제가 완료되었습니다.");
    }

    @Transactional
    public KeyChainDto.Payload decrypt(HttpServletRequest request, Long id) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        KeyChain keyChain = keyChainRepository.findById(id).get();
        if (!isAccessible(keyChain, email)) {
            throw new GlobalException(KeyChainExceptionCode.KEY_CHAIN_NOT_FOUND);
        }

        return KeyChainDto.Payload.from(keyChain, encryptionService);
    }

    private boolean isAccessible(KeyChain keyChain, String email) {
        return keyChain != null && !keyChain.isDeleted() && keyChain.getUser().getEmail().equals(email);
    }
}
