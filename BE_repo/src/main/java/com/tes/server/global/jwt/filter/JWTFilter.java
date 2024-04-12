package com.tes.server.global.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tes.server.domain.user.detail.CustomUserDetails;
import com.tes.server.domain.user.entity.UserEntity;
import com.tes.server.domain.user.entity.type.OauthProvider;
import com.tes.server.global.redis.dto.TokenDto;
import com.tes.server.global.redis.repository.RefreshTokenRepository;
import com.tes.server.global.redis.service.RedisService;
import com.tes.server.global.redis.dto.RefreshTokenDto;
import com.tes.server.global.Base.Response;
import com.tes.server.global.jwt.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final RefreshTokenRepository refreshTokenRepository;
    private ObjectMapper mapper = new ObjectMapper();

    // private CustomSuccessHandler customSuccessHandler;

    /*
     * WAS 접근시 바로 실행되는 메서드
     *
     * (1) 스웨거 경로로 들어왔다면, 바로 Controller로 이동
     * (2) Authorization 헤더가 존재하고 Bearer로 시작하는 확인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 회원 가입은 바로 비지니스 로직 처리
        // 로그인은 로그인 필터로 수행
        if (request.getRequestURI().startsWith("/api/auth/social") ||
                request.getRequestURI().startsWith("/api/auth/auto")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 스웨거 경로를 확인하고 필터 적용을 건너뛰기
        if (request.getRequestURI().startsWith("/api/swagger-ui") ||
                request.getRequestURI().startsWith("/api/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 존재하거나 Bearer로 시작했는지 확인.
        // 그 외 경로는 모두 되돌림.
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null!!");

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(
                    mapper.writeValueAsString(
                            Response.fail(403, "유효하지 않은 AccessToken입니다.", "A-001")));
            response.setStatus(403);
            return;

        }

        // 토큰 가져오기
        String accessToken = authorization.split(" ")[1];
        String refreshToken = request.getHeader("refreshToken");
        System.out.println("refreshToken is null? " + (refreshToken==null));

        // access 토큰만 가질 경우
        if (refreshToken == null) {

            // 만약 Access 토큰이 만료되었거나 유효하지 않는다면
            if (!jwtUtil.validateToken(accessToken)) {
                System.out.println("Access 토큰 검증 시도 중 문제 발생");

                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(
                        mapper.writeValueAsString(
                                Response.fail(401, "만료된 AccessToken입니다.", "A-002")));
                response.setStatus(401);
                return;
            }

            // Access 토큰이 유효하다면
            // 서브릿에게 전달
            else {
                // 토큰에서 userCode, OAuthProvider 획득
                String userCode = jwtUtil.getUserCode(accessToken);
                OauthProvider provider = jwtUtil.getOauthProvider(accessToken);

                // User 생성하여 값 설정
                UserEntity userEntity = UserEntity.builder()
                        .userCode(userCode)
                        .oauthProvider(provider)
                        .build();

                // UserDetails에 회원 정보 객체 담기
                CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

                // 스프링 시큐리티 인증 토큰 생성
                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, null);

                // 세션에 사용자 등록
                SecurityContextHolder.getContext().setAuthentication(authToken);

                filterChain.doFilter(request, response);
            }
        }

        // 둘 다 토큰을 가지고 있을 경우 재갱신 목적
        else {
            // Refresh 토큰도 만료될 경우
            if (!jwtUtil.validateToken(refreshToken)) {
                System.out.println("Refresh 토큰 검증 시도 중 문제 발생");

                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(
                        mapper.writeValueAsString(
                                Response.fail(401, "만료된 RefreshToken입니다.", "A-003")));
                response.setStatus(401);
                return;
            }

            // Refresh 토큰이 만료되지 않을 경우
            // Access와 Refresh 토큰을 재발급 (RTR 기법)
            else {
                String userCode = jwtUtil.getUserCode(refreshToken);
                OauthProvider provider = jwtUtil.getOauthProvider(refreshToken);

                // 이전 refresh 토큰인지 확인
                RefreshTokenDto getRefreshToken = refreshTokenRepository.findById(userCode).get();
                System.out.println(refreshToken);
                System.out.println(getRefreshToken.getRefreshToken());
                if (!refreshToken.equals(getRefreshToken.getRefreshToken())) {
                    Map<String, String> result = new TreeMap<>();
                    result.put("msg", "유효하지 않은 Refresh 토큰");

                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().println(
                            mapper.writeValueAsString(
                                    Response.fail(401, "만료된 RefreshToken입니다.", "A-003")));
                    response.setStatus(401);
                } else {
                    TokenDto tokenDTO = jwtUtil.generateToken(userCode, provider);
                    redisService.saveToken(userCode, tokenDTO.getRefreshToken());

                    Map<String, Object> result = new TreeMap<>();

                    result.put("accessToken", tokenDTO.getAccessToken());
                    result.put("refreshToken", tokenDTO.getRefreshToken());

                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().println(
                            mapper.writeValueAsString(
                                    Response.success(200, "재갱신 완료", "SUCCESS",result)));
                }
            }
        }
    }
}