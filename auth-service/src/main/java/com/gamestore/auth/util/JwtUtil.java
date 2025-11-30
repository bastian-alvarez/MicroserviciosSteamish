package com.gamestore.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(String email, Long userId, boolean isAdmin, boolean isModerator) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);
        claims.put("isAdmin", isAdmin);
        claims.put("isModerator", isModerator);
        
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    // Método sobrecargado para compatibilidad hacia atrás
    public String generateToken(String email, Long userId, boolean isAdmin) {
        return generateToken(email, userId, isAdmin, false);
    }
    
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
    
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }
    
    public Boolean isAdmin(String token) {
        return extractClaims(token).get("isAdmin", Boolean.class);
    }
    
    public Boolean isModerator(String token) {
        Boolean isMod = extractClaims(token).get("isModerator", Boolean.class);
        return isMod != null && isMod;
    }
    
    public Boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Valida el token JWT completamente:
     * - Verifica la firma (signature)
     * - Verifica la estructura del token
     * - Verifica que no esté expirado
     */
    public Boolean validateToken(String token) {
        try {
            // Intenta parsear el token para verificar firma y estructura
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // Verifica que no esté expirado
            if (claims.getExpiration().before(new Date())) {
                log.warn("Token JWT expirado");
                return false;
            }
            
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.error("Firma JWT inválida: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformado: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("Argumento ilegal para JWT: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error inesperado al validar token JWT: {}", e.getMessage());
            return false;
        }
    }
    
    public Long getExpirationTime() {
        return expiration;
    }
}

