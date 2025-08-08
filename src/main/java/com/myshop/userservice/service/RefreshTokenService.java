package com.myshop.userservice.service;

import com.myshop.userservice.repository.RefreshToken;
import com.myshop.userservice.repository.RefreshTokenRepository;
import com.myshop.userservice.repository.User;
import com.myshop.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenDurationMs;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isAfter(Instant.now())) {
            refreshTokenRepository.delete(token);
            return null;
        }
        return token;
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    public void deleteAllByUser(UUID id) {
        refreshTokenRepository.deleteAllByUser(id);
    }

    @Scheduled(cron = "@daily")
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredBefore(Instant.now());
    }
}
