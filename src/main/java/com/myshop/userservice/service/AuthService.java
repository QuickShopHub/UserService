package com.myshop.userservice.service;


import com.myshop.userservice.DTO.AuthDTO;
import com.myshop.userservice.repository.RefreshToken;
import com.myshop.userservice.repository.RefreshTokenRepository;
import com.myshop.userservice.repository.User;
import com.myshop.userservice.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(RefreshTokenService refreshTokenService, UserRepository userRepository, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public ResponseEntity<String> login(AuthDTO authDTO, HttpServletResponse response) {
        User user = userRepository.findByEmailAndPassword(authDTO.getEmail(), authDTO.getPassword());
        if(user == null){
            return ResponseEntity.badRequest().body("Неверный пароль");
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);



        String cookieValue = "refresh_token=" + refreshToken.getRefreshToken();
        String cookieAttributes =
                "; Path=/api/auth/" +
                        "; Max-Age=604800" +
                        "; HttpOnly" +
                        "; Secure" +
                        "; SameSite=Strict";
        response.addHeader("Set-Cookie", cookieValue + cookieAttributes);
        return ResponseEntity.ok(jwtService.generateToken(user));
    }

    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshTokenValue = extractRefreshTokenFromCookies(request);
        if (refreshTokenValue == null) {
            return ResponseEntity.status(401).body(null);
        }

        UUID id = refreshTokenRepository.findIdUserByRefreshToken(refreshTokenValue);

        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.badRequest().body("User not found");
        }

        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(refreshTokenValue);

        if(refreshTokenOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Login again");
        }

        RefreshToken refreshToken = refreshTokenOptional.get();

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        if(refreshToken == null){
            return ResponseEntity.badRequest().body("Login again");
        }

        refreshTokenService.deleteByToken(refreshToken.getRefreshToken());

        refreshToken = refreshTokenService.createRefreshToken(user);

        String cookieValue = "refresh_token=" + refreshToken.getRefreshToken();
        String cookieAttributes =
                "; Path=/api/auth/" +
                        "; Max-Age=604800" +
                        "; HttpOnly" +
                        "; Secure" +
                        "; SameSite=Strict";
        response.addHeader("Set-Cookie", cookieValue + cookieAttributes);
        return ResponseEntity.ok(jwtService.generateToken(user));

    }

}
