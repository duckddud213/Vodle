package com.tes.server.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tes.server.domain.auth.dto.AuthDto;
import com.tes.server.domain.auth.dto.AuthMyVodleGetResDto;
import com.tes.server.domain.auth.service.AuthService;
import com.tes.server.domain.user.detail.CustomUserDetails;
import com.tes.server.domain.user.entity.UserEntity;
import com.tes.server.domain.user.entity.type.OauthProvider;
import com.tes.server.global.Base.BaseResponseBody;
import com.tes.server.global.Base.DataResponseBody;
import com.tes.server.global.exception.ErrorCode;
import com.tes.server.global.exception.Exceptions;
import com.tes.server.global.redis.dto.TokenDto;
import com.tes.server.global.redis.service.RedisService;
import com.tes.server.global.jwt.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Tag(name = "1. 인증", description = "사용자 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private ObjectMapper mapper = new ObjectMapper();
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${spring.hmac.secret}")
    private String secretKey;

    /**
     * 1. 로그인 또는 회원가입 메서드
     * <p>
     * 역할: Native App으로부터 (회원 정보 + (회원정보) by HMAC) 값을 받아 인증을 처리한다.
     *
     * @param authDto
     * @return
     */
    @Operation(summary = "로그인 또는 회원가입 메서드 인증", description = "\n\n 사용자는 소셜 로그인 또는 회원가입을 인증한다.")
    @PostMapping("/social")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "ALL", description = "성공 \n\n Success 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DataResponseBody.class),
                            examples = {
                                    @ExampleObject(name = "로그인 성공", description = "사용자는 로그인을 통해 위와 같은 응답 데이터를 받는다.", value = """ 
                                        { 
                                            "status" : 200, 
                                            "message" : "로그인 성공",
                                            "code" : "SUCCESS",
                                            "data" : {
                                                "accessToken": "asdasdasdasdasd8qwrt81tyoisu1293129ed120asdyi102aksjhdjiqjwid1",
                                                "refreshToken": "asfuywefguhiury2938hrisdhisudhfi2yh8e7ufhiwuhfiwehfisdhfjkshdfuiwehfuiehuiwhfiue"
                                            }
                                        } 
                                    """),
                                    @ExampleObject(name = "회원가입 성공", description = "사용자는 회원가입을 통해 위와 같은 응답 데이터를 받는다.", value = """ 
                                        { 
                                            "status" : 201, 
                                            "message" : "회원가입 성공",
                                            "code" : "SUCCESS",
                                            "data" : {
                                                "accessToken": "asdasdasdasdasd8qwrt81tyoisu1293129ed120asdyi102aksjhdjiqjwid1",
                                                "refreshToken": "asfuywefguhiury2938hrisdhisudhfi2yh8e7ufhiwuhfiwehfisdhfjkshdfuiwehfuiehuiwhfiue"
                                            }
                                        } 
                                    """),
                                    @ExampleObject(name = "로그인 & 회원가입 실패", description = "사용자는 로그인 또는 회원가입을 실패한다면 위와 같은 응답 데이터를 받는다.", value = """ 
                                        { 
                                            "status" : 403, 
                                            "message" : "로그인 또는 회원가입 실패",
                                            "code" : "A-숫자"
                                        } 
                                    """)
                            })),
    })
    public ResponseEntity<?> doSocial(@RequestBody AuthDto authDto) {
        try {
            // (1) 먼저, 사용자 정보를 암호화하여 인증을 수행한다.
            String serverHMAC = calculateHmac(authDto, secretKey);

            // (1-1) 만약 일치하지 않다면, 올바른 클라이언트가 아니다.
            if (!authDto.getSignature().equals(serverHMAC)) {
                throw new Exceptions(ErrorCode.USER_INVALID_HMAC);
            }
        }
        // (1-2) 만약 비밀키가 다르면, 올바른 클라이언트가 아니다.
        catch (Exception e) {
            throw new Exceptions(ErrorCode.USER_INVALID_SECRET_KEY);
        }

        Map<String, Object> result = new TreeMap<>();
        try {
            // (2) 인증이 완료되면, 등록된 사용자인지 확인한다.
            UserEntity entity = authService.doLogin(authDto);

            // (2-1) 등록된 사용자라면, 200을 응답한다.
            if (entity != null) {
                TokenDto tokenDto = getTokenDto(authDto, jwtUtil, redisService);
                result.put("accessToken", tokenDto.getAccessToken());
                result.put("refreshToken", tokenDto.getRefreshToken());

                return ResponseEntity.status(HttpStatus.OK).body(DataResponseBody.of(200, "로그인 완료", "SUCCESS", result));
            }

            // (2-2) 등록되지 않은 사용자라면, 회원가입을 수행한다.
            else {
                authService.doJoin(authDto);
                TokenDto tokenDto = getTokenDto(authDto, jwtUtil, redisService);
                result.put("accessToken", tokenDto.getAccessToken());
                result.put("refreshToken", tokenDto.getRefreshToken());

                return ResponseEntity.status(HttpStatus.CREATED).body(DataResponseBody.of(201, "회원가입 완료.", "SUCCESS", result));
            }

        }
        // (2-3) 디비에 문제 생길 경우
        catch (Exceptions e) {
            throw new Exceptions(ErrorCode.USER_DISCONNECTED_DB);
        }
    }

    /**
     * 2. 자신의 낙서 목록 조회
     * <p>
     * 역할: 자신이 등록한 낙서들을 조회한다.
     */
    // 자신이 등록한 낙서 목록 조회
    @Operation(summary = "자신이 등록한 낙서 목록 조회", description = "\n\n 자신이 등록한 낙서들을 조회가능하다.")
    @GetMapping("/me")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 자신이 등록한 낙서 목록 반환",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponseBody.class),
                    examples = {
                            @ExampleObject(name = "자신이 등록한 낙서 목록 조회 성공", description = "자신이 등록한 낙서들을 조회가능하다.", value = """ 
                        { 
                            "status" : 200, 
                            "message" : "등록한 낙서 목록 조회 성공",
                            "code" : "SUCCESS",
                            "data" : [
                                {
                                    "vodleId": 1,
                                    "address": "경북 구미시 진평길 79-3",
                                    "createdDate": "2021년 11월 8일 11시 44분"
                                }
                            ]
                        } 
                        """)
                    })
    )
    public ResponseEntity<? extends DataResponseBody> getMyVodle(
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        log.info("등록한 낙서 목록 조회 URL 매핑 : {}", "OK");

        log.info("등록한 낙서 목록 조회 시작 : {}", "START");
        List<AuthMyVodleGetResDto> authMyVodleGetResDtoList = authService.getMyVodle(user.getUserEntity().getUserCode());
        log.info("등록한 낙서 목록 조회 완료 : {}", "COMPLETE");

        return ResponseEntity.status(HttpStatus.OK).body(DataResponseBody.of(200,"등록한 낙서 목록 조회 완료","SUCCESS",authMyVodleGetResDtoList));
    }

    /**
     * 3. 유저 삭제 메서드
     * <p>
     * 역할:
     *
     * @param authentication
     * @return
     */
    @Operation(summary = "사용자 삭제", description = "\n\n 사용자는 회원 탈퇴를 할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "ALL", description = "성공 \n\n Success 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DataResponseBody.class),
                            examples = {
                                    @ExampleObject(name = "사용자 조회 성공", value = """ 
                                        { 
                                            "status" : 200, 
                                            "message" : "사용자 삭제 성공",
                                            "code" : "SUCCESS"
                                        } 
                                    """, description = "사용자는 헤더에 AccessToken을 이용하여 위 자신의 정보를 삭제한다"),
                                    @ExampleObject(name = "사용자 조회 실패", description = "사용자는 삭제를 실패한다면 위와 같은 응답 데이터를 받는다.", value = """ 
                                        { 
                                            "status" : 400, 
                                            "message" : "사용자 삭제 실패",
                                            "code" : "U-숫자"
                                        } 
                                    """)
                            })
            )
    })
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(Authentication authentication) throws IOException {
        // UserDetails 인터페이스를 구현한 구체적인 사용자 세부 정보 객체
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 사용자 세부 정보 객체로 찾은 UserEntity 객체
        authService.deleteUser(userDetails.getUsername());

        // 추후 수정 예정
        if (userDetails.getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(200, "삭제 완료.", "SUCCESS"));
        } else {
            throw new Exceptions(ErrorCode.USER_NOT_EXIST);
        }
    }

    /**
     * 4. 자동 로그인 메서드
     * <p>
     * 역할:
     *
     * @param accessToken
     * @return
     */
    @Operation(summary = "자동 로그인", description = "\n\n 사용자는 자동 로그인을 할 수 있다.")
    @PostMapping("/auto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "ALL", description = "성공 \n\n Success 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DataResponseBody.class),
                            examples = {
                                    @ExampleObject(name = "자동 로그인 성공", description = "사용자는 자동 로그인을 통해 위와 같은 응답 데이터를 받는다.", value = """ 
                                        { 
                                            "status" : 200, 
                                            "message" : "자동 로그인 성공",
                                            "code" : "SUCCESS",
                                            "data" : {
                                                "accessToken": "asdasdasdasdasd8qwrt81tyoisu1293129ed120asdyi102aksjhdjiqjwid1",
                                                "refreshToken": "asfuywefguhiury2938hrisdhisudhfi2yh8e7ufhiwuhfiwehfisdhfjkshdfuiwehfuiehuiwhfiue"
                                            }
                                        } 
                                    """),
                                    @ExampleObject(name = "자동 로그인 실패", description = "사용자는 자동 로그인을 실패한다면 위와 같은 응답 데이터를 받는다.", value = """ 
                                        { 
                                            "status" : 403,
                                            "message" : "자동 로그인 실패",
                                            "code" : "A-004"
                                        } 
                                    """),
                                    @ExampleObject(name = "유효하지 않은 AccessToken", description = "사용자는 유효하지 않은 토큰을 전달한다면 위와 같은 응답 데이터를 받는다.", value = """ 
                                        { 
                                            "status" : 403,
                                            "message" : "유효하지 않은 AccessToken",
                                            "code" : "A-001"
                                        } 
                                    """)
                            })),
    })
    public ResponseEntity<?> doAutoLogin(HttpServletRequest request) {
        String tmp[] = request.getHeader("Authorization").split(" ");
        String token = tmp[1];

        // (1) accessToken 검증이 실패할 경우, "AUTO_LOGIN_FAIL"
        // if(!jwtUtil.validateToken(token)) throw new Exceptions(ErrorCode.AUTO_LOGIN_FAIL);

        try {
            // (1-1) 검증이 성공한 경우, 유저 코드와 제공자 가져오기
            String userCode = jwtUtil.getUserCode(token);
            OauthProvider provider = jwtUtil.getOauthProvider(token);

            // (1-2) RTR 기법으로 토큰 생성
            TokenDto tokenDto = jwtUtil.generateToken(userCode, provider);

            Map<String, Object> result = new TreeMap<>();
            result.put("accessToken", tokenDto.getAccessToken());
            result.put("refreshToken", tokenDto.getRefreshToken());

            return ResponseEntity.status(HttpStatus.OK).body(DataResponseBody.of(200, "자동 로그인 완료", "SUCCESS", result));
        }
        // (1-3) 만약 디비 연결이 실패되면, "USER_DISCONNECTED_DB"
        catch (Exceptions e) {
            throw new Exceptions(ErrorCode.USER_DISCONNECTED_DB);
        }
    }

    /**
     * @param dto
     * @param secretKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String calculateHmac(AuthDto dto, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String data = dto.getUserCode() + "&" + dto.getProvider().toString();

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static TokenDto getTokenDto(AuthDto authDto, JWTUtil jwtUtil, RedisService redisService) {
        // Access와 Refresh 토큰을 생성
        TokenDto tokenDTO = jwtUtil.generateToken(authDto.getUserCode(), authDto.getProvider());

        // redis에 refresh 토큰 저장
        redisService.saveToken(authDto.getUserCode(), tokenDTO.getRefreshToken());

        return tokenDTO;
    }
}