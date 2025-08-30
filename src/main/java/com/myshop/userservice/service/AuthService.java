package com.myshop.userservice.service;


import com.myshop.userservice.DTO.AuthDTO;
import com.myshop.userservice.DTO.ResponseDTO;
import com.myshop.userservice.config.EmailEncryptionUtil;
import com.myshop.userservice.repository.RefreshToken;
import com.myshop.userservice.repository.RefreshTokenRepository;
import com.myshop.userservice.repository.User;
import com.myshop.userservice.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User checkAuth(String password, String Email){
        try{
            String emailCode = EmailEncryptionUtil.encodeEmail(Email);
            User user = userRepository.findUserByEmail(emailCode);
            if(passwordEncoder.matches(password, user.getPassword())){
                return user;
            }
            else return null;
        }
        catch (Exception e){
            return null;
        }
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public ResponseEntity<ResponseDTO> login(AuthDTO authDTO, HttpServletResponse response) {

        User user = checkAuth(authDTO.getPassword(), authDTO.getEmail());
        ResponseDTO responseDTO = new ResponseDTO();
        if(user == null){
            responseDTO.setMessage("Неверный email или пароль");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        refreshTokenRepository.save(refreshToken);

        responseDTO.setToken(getTokens(response, user, refreshToken));
        responseDTO.setUser(user);

        return ResponseEntity.ok(responseDTO);
    }

    private String getTokens(HttpServletResponse response, User user, RefreshToken refreshToken) {
        String cookieValue = "refresh_token=" + refreshToken.getRefreshToken();
        String cookieAttributes =
                "; Path=/" +
                        "; Max-Age=604800" +
                        "; HttpOnly" +
                        "; SameSite=Strict";
        response.addHeader("Set-Cookie", cookieValue + cookieAttributes);
        if(user.isAdmin()){
            return jwtService.generateToken(user, List.of("USER", "ADMIN"));
        }
        return jwtService.generateToken(user, List.of("USER"));
    }

    public ResponseEntity<ResponseDTO> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        ResponseDTO responseDTO = new ResponseDTO();
        String refreshTokenValue = extractRefreshTokenFromCookies(request);
        if (refreshTokenValue == null) {
            return ResponseEntity.status(401).body(null);
        }

        UUID id = refreshTokenRepository.findIdUserByRefreshToken(refreshTokenValue);

        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            responseDTO.setMessage("User not found");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(refreshTokenValue);

        if(refreshTokenOptional.isEmpty()){
            responseDTO.setMessage("Login again");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        RefreshToken refreshToken = refreshTokenOptional.get();

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        if(refreshToken == null){
            responseDTO.setMessage("Login again");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        refreshTokenService.deleteByToken(refreshToken.getRefreshToken());
        refreshToken = refreshTokenService.createRefreshToken(user);
        responseDTO.setUser(user);
        responseDTO.setToken(getTokens(response, user, refreshToken));
        responseDTO.setMessage("ok");
        return ResponseEntity.ok(responseDTO);
    }

    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        String refreshTokenValue = extractRefreshTokenFromCookies(request);
        if (refreshTokenValue == null) {
            return ResponseEntity.status(401).body(null);
        }
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(refreshTokenValue);
        if(refreshTokenOptional.isEmpty()){
            return ResponseEntity.status(401).body(null);
        }
        refreshTokenRepository.delete(refreshTokenOptional.get());

        String cookieValue = "refresh_token=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=Strict";
        response.addHeader("Set-Cookie", cookieValue);

        return ResponseEntity.ok().body("Logout");
    }

}
