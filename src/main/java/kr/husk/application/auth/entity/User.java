package kr.husk.application.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.husk.application.auth.type.OAuthProvider;
import kr.husk.common.entity.BaseEntity;

@Entity
public class User extends BaseEntity {
    @Column(name = "email", nullable = false, updatable = false, length = 50)
    private String email;

    @Column(name = "password", length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false)
    private OAuthProvider oAuthProvider;
}
