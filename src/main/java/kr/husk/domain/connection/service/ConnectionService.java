package kr.husk.domain.connection.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.husk.application.connection.dto.ConnectionInfoDto;
import kr.husk.common.exception.GlobalException;
import kr.husk.common.jwt.util.JwtProvider;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.service.UserService;
import kr.husk.domain.connection.entity.Connection;
import kr.husk.domain.connection.exception.ConnectionExceptionCode;
import kr.husk.domain.connection.repository.ConnectionRepository;
import kr.husk.domain.keychain.entity.KeyChain;
import kr.husk.domain.keychain.exception.KeyChainExceptionCode;
import kr.husk.domain.keychain.service.KeyChainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final KeyChainService keyChainService;
    private final ConnectionRepository connectionRepository;

    @Transactional
    public ConnectionInfoDto.Response create(HttpServletRequest request, ConnectionInfoDto.Request dto) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        User user = userService.read(email);
        KeyChain keyChain = keyChainService.read(user, dto.getKeyChainName());

        if (keyChain == null) {
            throw new GlobalException(KeyChainExceptionCode.KEY_CHAIN_NOT_FOUND);
        }

        log.info("사용자 {}의 커넥션 {}이 저장되었습니다.", email, dto.getName());
        Connection connection = dto.toEntity(user, keyChain);
        connectionRepository.save(connection);

        return ConnectionInfoDto.Response.of("SSH 커넥션이 성공적으로 저장되었습니다.");
    }

    @Transactional
    public List<ConnectionInfoDto.Summary> read(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        User user = userService.read(email);
        return ConnectionInfoDto.Summary.from(user.getConnections());
    }

    @Transactional
    public Connection read(Long id) {
        return connectionRepository.findById(id)
                .filter(connection -> !connection.isDeleted())
                .orElseThrow(() -> new GlobalException(ConnectionExceptionCode.CONNECTION_NOT_FOUND));
    }

    @Transactional
    public ConnectionInfoDto.Response connect(HttpServletRequest request, Long id) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        Connection connection = read(id);
        if (!isAccessible(connection, email)) {
            throw new GlobalException(ConnectionExceptionCode.CONNECTION_NOT_FOUND);
        }

        return ConnectionInfoDto.Response.of("커넥션 접속에 성공하였습니다.");
    }

    @Transactional
    public ConnectionInfoDto.Response update(HttpServletRequest request, Long id, ConnectionInfoDto.Request dto) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        Connection connection = read(id);
        if (!isAccessible(connection, email)) {
            throw new GlobalException(ConnectionExceptionCode.CONNECTION_NOT_FOUND);
        }

        User user = userService.read(email);
        KeyChain keyChain = keyChainService.read(user, dto.getKeyChainName());
        if (!checkKeyChain(keyChain)) {
            throw new GlobalException(KeyChainExceptionCode.KEY_CHAIN_NOT_FOUND);
        }

        connection.update(dto.getName(), dto.getUsername(), dto.getHost(), dto.getPort(), keyChain);

        return ConnectionInfoDto.Response.of("커넥션 수정에 성공하였습니다.");
    }

    @Transactional
    public ConnectionInfoDto.Response delete(HttpServletRequest request, Long id) {
        String accessToken = jwtProvider.resolveToken(request);
        String email = jwtProvider.getEmail(accessToken);

        Connection connection = read(id);
        if (!isAccessible(connection, email)) {
            throw new GlobalException(ConnectionExceptionCode.CONNECTION_NOT_FOUND);
        }

        if (connection.isDeleted()) {
            throw new GlobalException(ConnectionExceptionCode.CONNECTION_NOT_FOUND);
        }

        connection.delete();

        log.info("사용자 {}의 {}번 커넥션({})이 삭제되었습니다.", email, id, connection.getName());
        return ConnectionInfoDto.Response.of("커넥션 삭제가 완료되었습니다.");
    }

    private boolean isAccessible(Connection connection, String email) {
        return connection.getUser().getEmail().equals(email);
    }

    private boolean checkKeyChain(KeyChain keyChain) {
        return keyChain != null && !keyChain.isDeleted();
    }
}
