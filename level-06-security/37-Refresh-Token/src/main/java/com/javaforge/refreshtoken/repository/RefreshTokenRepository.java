package com.javaforge.refreshtoken.repository;

import com.javaforge.refreshtoken.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByFamilyId(String familyId);
}
