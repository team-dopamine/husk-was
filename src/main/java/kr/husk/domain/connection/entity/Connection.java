package kr.husk.domain.connection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.husk.common.entity.BaseEntity;
import kr.husk.domain.auth.entity.User;
import kr.husk.domain.keychain.entity.KeyChain;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity(name = "connection")
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Connection extends BaseEntity {
    //user_id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    // keychain_id
    @ManyToOne
    @JoinColumn(name = "keychain_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private KeyChain keyChain;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "host", nullable = false, length = 15)
    private String host;

    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "port", nullable = false, length = 5)
    @ColumnDefault(value = "22")
    private String port;

    @Builder
    public Connection(User user, KeyChain keyChain, String name, String host, String username, String port) {
        this.user = user;
        this.keyChain = keyChain;
        this.name = name;
        this.host = host;
        this.username = username;
        this.port = port;
    }
}
