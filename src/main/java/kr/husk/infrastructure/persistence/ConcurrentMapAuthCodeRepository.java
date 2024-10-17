package kr.husk.infrastructure.persistence;

import kr.husk.domain.auth.repository.AuthCodeRepository;
import kr.husk.infrastructure.config.AuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ConcurrentMapAuthCodeRepository implements AuthCodeRepository {
    private final AuthConfig authConfig;
    private final ConcurrentHashMap<String, String> authCodeMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void create(String key, String code) {
        authCodeMap.put(authConfig.getKeyPrefix() + key, code);
        scheduledExecutorService.schedule(() -> authCodeMap.remove(key), authConfig.getCodeExpiration(), TimeUnit.MILLISECONDS);
    }

    @Override
    public String read(String key) {
        return authCodeMap.get(authConfig.getKeyPrefix() + key);
    }

    @Override
    public void delete(String key) {
        authCodeMap.remove(authConfig.getKeyPrefix() + key);
    }
}
