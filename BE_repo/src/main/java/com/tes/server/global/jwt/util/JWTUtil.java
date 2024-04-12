package com.tes.server.global.jwt.util;

import com.tes.server.domain.user.entity.type.OauthProvider;
import com.tes.server.domain.user.repository.UserRepository;
import com.tes.server.global.redis.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private SecretKey secretKey;
    private final static long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 12 * 30;
    private final static long REFRESH_TOKEN_VALIDITY_SECONDS = 86400;

    public JWTUtil(RedisTemplate<String, String> redisTemplate, @Value("${spring.jwt.secret}") String secret,
                   UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.userRepository = userRepository;
    }

    /**
     * 로그인 시 Access와 Refresh 저장하는 메서드
     *
     * @param userCode
     * @return Access와 Refresh 토큰
     */
    public TokenDto generateToken(String userCode, OauthProvider oauthProvider) {
        // Access Token 생성
        String accessToken = Jwts.builder()
                .claim("userCode", userCode)
                .claim("oauthProvider", oauthProvider)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000 * 2))
                .signWith(secretKey)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .claim("userCode", userCode)
                .claim("oauthProvider", oauthProvider)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_SECONDS * 1000 * 2))
                .signWith(secretKey)
                .compact();

        return new TokenDto(accessToken, refreshToken);
    }

    /**
     * 토큰 유효성 체크
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        } catch (Exception e){
            System.out.println("검증 시도 중 에러 발생");
        }
        return false;
    }

    public String getUserCode(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("userCode", String.class);
    }
    public OauthProvider getOauthProvider(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        String oauthProviderString = claims.get("oauthProvider", String.class);
        OauthProvider oauthProvider = OauthProvider.valueOf(oauthProviderString);

        return oauthProvider;
    }
}