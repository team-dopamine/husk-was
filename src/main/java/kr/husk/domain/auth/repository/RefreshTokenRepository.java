package kr.husk.domain.auth.repository;

import java.util.Optional;

public interface RefreshTokenRepository {
    void create(String email);

    Optional<String> read(String email);

    void delete(String email);
}
