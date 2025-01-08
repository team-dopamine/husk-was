package kr.husk.domain.auth.repository;

import kr.husk.domain.auth.entity.User;
import kr.husk.domain.auth.type.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM user u WHERE u.email = :email And u.oAuthProvider = :oAuthProvider")
    Optional<User> findByEmailAndOAuthProvider(String email, OAuthProvider oAuthProvider);

}
