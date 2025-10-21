package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.PasswordResetToken;
import cz.upce.fei.redsys.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByCode(String code);
    void deleteByUserAndExpiresAtBefore(User user, Instant time);
}