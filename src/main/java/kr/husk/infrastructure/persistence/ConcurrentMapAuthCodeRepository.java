package kr.husk.infrastructure.persistence;

import kr.husk.domain.auth.repository.AuthCodeRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
public class ConcurrentMapAuthCodeRepository implements AuthCodeRepository {
    private final ConcurrentHashMap<String, String> authCodeMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void create(String key, String code, long expireTime) {
        authCodeMap.put(key, code);
        scheduledExecutorService.schedule(() -> authCodeMap.remove(key), expireTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public String read(String key) {
        return authCodeMap.get(key);
    }

    @Override
    public void delete(String key) {
        authCodeMap.remove(key);
    }
}
