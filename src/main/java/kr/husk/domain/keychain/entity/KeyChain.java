package kr.husk.domain.keychain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.husk.common.entity.BaseEntity;
import kr.husk.domain.auth.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "keychain")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeyChain extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "name", nullable = false, length = 20)
    private String name;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public KeyChain(User user, String name, String content) {
        this.user = user;
        this.name = name;
        this.content = content;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
