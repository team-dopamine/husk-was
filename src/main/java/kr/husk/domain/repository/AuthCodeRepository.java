package kr.husk.domain.repository;

public interface AuthCodeRepository {
    void create(String key, String code, long expireTime);
    String read(String key);
    void delete(String key);
}
