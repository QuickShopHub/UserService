package com.myshop.userservice.service;


import com.myshop.userservice.repository.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {



    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs; // например, 900000 = 15 минут

    private final ResourceLoader resourceLoader;


    private PrivateKey privateKey;
    private PublicKey publicKey;


    public JwtService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        try {

            privateKey = this.loadPrivateKey();
            publicKey = this.loadPublicKey();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PrivateKey loadPrivateKey() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:security/private.pem");

        try {
            InputStream is = resource.getInputStream();
            String privateKeyPEM = new String(is.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private PublicKey loadPublicKey() throws Exception {

        Resource resource = resourceLoader.getResource("classpath:security/public.pem");

        try {
            InputStream is = resource.getInputStream();


            String publicKeyPEM = new String(is.readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String generateToken(User user, List<String> roles) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return UUID.fromString(subject);
    }

    public boolean isTokenValid(String token, User user) {
        final UUID userId = getUserIdFromToken(token);
        return (userId.equals(user.getId()) && !isTokenExpired(token));
    }


    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }


    private Date getExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}