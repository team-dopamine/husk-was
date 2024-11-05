package kr.husk.domain.auth.entity;

import jakarta.persistence.*;
import kr.husk.common.entity.BaseEntity;
import kr.husk.domain.auth.type.OAuthProvider;
import kr.husk.domain.connection.entity.Connection;
import kr.husk.domain.keychain.entity.KeyChain;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Column(name = "email", nullable = false, updatable = false, length = 50)
    private String email;

    @Column(name = "password", length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false)
    private OAuthProvider oAuthProvider;

    @OneToMany(mappedBy = "user")
    private List<KeyChain> keyChains;

    @OneToMany(mappedBy = "user")
    private List<Connection> connections;

    @Builder
    public User(String email, String password, OAuthProvider oAuthProvider, List<KeyChain> keyChains, List<Connection> connections) {
        this.email = email;
        this.password = password;
        this.oAuthProvider = oAuthProvider;
        this.keyChains = keyChains;
        this.connections = connections;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
}
