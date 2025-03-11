package kr.husk.domain.connection.repository;

import kr.husk.domain.connection.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}
