package kr.husk.infrastructure.persistence;

import kr.husk.common.jwt.util.JwtProvider;
import kr.husk.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ConcurrentMapRefreshRefreshTokenRepository implements RefreshTokenRepository {

    private final JwtProvider jwtProvider;
    private final ConcurrentHashMap<String, String> refreshTokenMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void create(String email, String token) {
        refreshTokenMap.put(email, token);

        long expirationInMillis = jwtProvider.getExpirationTime(token);
        long delayInMillis = expirationInMillis - System.currentTimeMillis();

        scheduledExecutorService.schedule(() -> refreshTokenMap.remove(email), delayInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<String> read(String email) {
        String refreshToken = refreshTokenMap.get(email);

        if (refreshToken == null) {
            return Optional.empty();
        }

        if (jwtProvider.validateToken(refreshToken)) {
            refreshTokenMap.remove(email);
            return Optional.empty();
        }

        return Optional.of(refreshToken);
    }

    @Override
    public void delete(String email) {
        refreshTokenMap.remove(email);
    }

}