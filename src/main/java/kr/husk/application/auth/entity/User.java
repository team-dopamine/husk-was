package kr.husk.application.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import kr.husk.application.auth.type.OAuthProvider;
import kr.husk.application.connection.entity.Connection;
import kr.husk.application.keychain.entity.KeyChain;
import kr.husk.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "user")
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
}
