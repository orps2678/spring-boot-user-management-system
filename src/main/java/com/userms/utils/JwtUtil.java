package com.userms.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private Long jwtExpiration;

    /**
     * 取得簽名密鑰
     * 新版本使用 SecretKey 取代 Key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     *
     * @param username 使用者名稱
     * @return JWT Token
     */
    public String generateToken(String username) {
        return createToken(username);
    }

    /**
     * 建立 Token
     * 使用新的 builder API，移除已棄用的 SignatureAlgorithm
     */
    private String createToken(String subject) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtExpiration, ChronoUnit.MILLIS);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 從 Token 中取得使用者名稱
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 從 Token 中取得到期時間
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 從 Token 中取得特定 Claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析 Token 取得所有 Claims
     * 使用新的 parser API
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.warn("Failed to parse JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * 檢查 Token 是否過期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 驗證 Token 有效性
     */
    public Boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return username.equals(tokenUsername) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 檢查 Token 是否即將過期（30分鐘內）
     * 用於決定是否需要刷新 Token
     */
    public Boolean isTokenNearExpiry(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            // 檢查是否在30分鐘內過期
            long thirtyMinutes = 30 * 60 * 1000;
            return expiration.getTime() - now.getTime() < thirtyMinutes;
        } catch (Exception e) {
            log.warn("Error checking token near expiry: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 從現有的 Token 刷新生成新 Token
     * 保持相同的用戶信息，但更新過期時間
     */
    public String refreshToken(String token) {
        try {
            // 驗證原 Token 結構是否有效（允許已過期）
            Claims claims = getAllClaimsFromTokenIgnoringExpiration(token);
            String username = claims.getSubject();

            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Token 中沒有用戶名信息");
            }

            // 生成新的 Token
            String newToken = generateToken(username);
            log.info("Token 刷新成功 for user: {}", username);
            return newToken;

        } catch (Exception e) {
            log.error("Token 刷新失敗: {}", e.getMessage());
            throw new RuntimeException("無法刷新 Token: " + e.getMessage());
        }
    }

    /**
     * 獲取 Token 中的所有 Claims（忽略過期檢查）
     * 用於 Token 刷新場景
     */
    private Claims getAllClaimsFromTokenIgnoringExpiration(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // Token 已過期，但我們仍然可以獲取其 Claims 用於刷新
            log.debug("Token expired but extracting claims for refresh: {}", e.getMessage());
            return e.getClaims();
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw new RuntimeException("Token 格式無效");
        }
    }

    /**
     * 檢查 Token 是否可以刷新
     * Token 必須結構有效，但可以已過期（在合理範圍內）
     */
    public Boolean canTokenBeRefreshed(String token) {
        try {
            Claims claims = getAllClaimsFromTokenIgnoringExpiration(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();

            // 允許過期後24小時內刷新
            long twentyFourHours = 24 * 60 * 60 * 1000;
            return (now.getTime() - expiration.getTime()) < twentyFourHours;
        } catch (Exception e) {
            log.warn("Cannot determine if token can be refreshed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 取得 Token 剩餘有效時間（毫秒）
     */
    public Long getRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            log.warn("Error getting remaining time: {}", e.getMessage());
            return 0L;
        }
    }

    /**
     * 檢查 Token 格式是否正確
     */
    public Boolean isValidFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
}