package kr.husk.infrastructure.persistence;

import kr.husk.domain.auth.repository.OAuthTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ConcurrentMapOAuthTokenRepository implements OAuthTokenRepository {
    private final ConcurrentHashMap<String, String> oAuthTokenMap = new ConcurrentHashMap<>();

    @Override
    public void create(String email, String oAuthToken) {
        oAuthTokenMap.put(email, oAuthToken);
    }

    @Override
    public String read(String email) {
        return oAuthTokenMap.get(email);
    }

    @Override
    public void delete(String email) {
        oAuthTokenMap.remove(email);
    }
}
