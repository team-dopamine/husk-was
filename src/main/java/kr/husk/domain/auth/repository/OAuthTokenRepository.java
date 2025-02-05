package kr.husk.domain.auth.repository;

public interface OAuthTokenRepository {
    void create(String email, String oAuthToken);

    String read(String email);

    void delete(String email);
}
