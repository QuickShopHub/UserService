package com.myshop.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Query(value = "SELECT * FROM refresh_tokens WHERE refresh_token = :token",  nativeQuery = true)
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query(value = "DELETE FROM refresh_tokens WHERE user_id = :user_id", nativeQuery = true)
    void deleteAllByUser(UUID user_id);

    @Modifying
    @Query(value = "DELETE FROM refresh_tokens WHERE expires_at < :instant", nativeQuery = true)
    void deleteAllExpiredBefore(Instant instant);

    @Query(value = "SELECT * FROM refresh_tokens WHERE user_id=:user_id", nativeQuery = true)
    RefreshToken findByUserId(UUID user_id);

    @Query(value = "SELECT user_id FROM refresh_tokens WHERE refresh_token=:refreshToken",  nativeQuery = true)
    UUID findIdUserByRefreshToken(String refreshToken);

}
