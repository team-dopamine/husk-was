package kr.husk.domain.keychain.entity;

import jakarta.persistence.*;
import kr.husk.common.entity.BaseEntity;
import kr.husk.domain.auth.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity(name = "keychain")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeyChain extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "name", nullable = false, length = 20)
    private String name;
    @Column(name = "content", nullable = false, length = 1024)
    private String content;

    @Builder
    public KeyChain(User user, String name, String content) {
        this.user = user;
        this.name = name;
        this.content = content;
    }
}
