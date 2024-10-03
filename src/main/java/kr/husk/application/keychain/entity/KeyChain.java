package kr.husk.application.keychain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.husk.application.auth.entity.User;
import kr.husk.common.entity.BaseEntity;

@Entity(name = "keychain")
public class KeyChain extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "name", nullable = false, length = 20)
    private String name;
    @Column(name = "content", nullable = false, length = 1024)
    private String content;
}
