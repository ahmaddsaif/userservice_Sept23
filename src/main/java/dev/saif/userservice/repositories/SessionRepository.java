package dev.saif.userservice.repositories;

import dev.saif.userservice.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByToken(String token);
}
