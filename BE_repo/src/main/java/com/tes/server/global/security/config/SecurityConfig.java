package com.tes.server.global.security.config;

import com.tes.server.global.jwt.filter.JWTFilter;
import com.tes.server.global.redis.repository.RefreshTokenRepository;
import com.tes.server.global.redis.service.RedisService;
import com.tes.server.global.jwt.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisService redisService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // RESTful API목적이고 STATELESS한 특성이기 때문에 csrf에 대한 보호가 필요없음
        http
                .csrf((auth) -> auth.disable());

        // JSON WEB TOKEN을 사용하므로, 필요없음
        http
                .formLogin((auth) -> auth.disable());

        // HTTP 기본 인증은 사용자 이름과 암호를 평문으로 전송하기 때문에 disable 설정
        http
                .httpBasic((auth) -> auth.disable());

        // .permitAll()로 선언된 경로는 모두 허가
        // 그 외 경로는 인증 필요
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/auth/social", "/auth/auto").permitAll()
                        .anyRequest().authenticated());

        // 세션을 사용하지 않고 바로 인증만 거치기 때문에 STATELESS 선언
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWT Filter 추가
        http.addFilterBefore(new JWTFilter(jwtUtil, redisService, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}