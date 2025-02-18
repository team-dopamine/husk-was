package kr.husk.domain.keychain.repository;

import kr.husk.domain.keychain.entity.KeyChain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyChainRepository extends JpaRepository<KeyChain, Long> {
}
