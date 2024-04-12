package com.tes.server.domain.auth.service;


import com.tes.server.domain.auth.dto.AuthDto;
import com.tes.server.domain.auth.dto.AuthMyVodleGetResDto;
import com.tes.server.domain.user.entity.UserEntity;
import com.tes.server.domain.user.repository.UserRepository;
import com.tes.server.domain.vodle.entity.VodleEntity;
import com.tes.server.domain.vodle.repository.VodleRepository;
import com.tes.server.global.jwt.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final VodleRepository vodleRepository;
    private final JWTUtil jwtUtil;

    public UserEntity doLogin(AuthDto dto) {
        UserEntity entity = userRepository.findByUserCode(dto.getUserCode());
        return entity;
    }

    public boolean doJoin(AuthDto dto){
        UserEntity user = UserEntity.builder()
                .userCode(dto.getUserCode())
                .oauthProvider(dto.getProvider()) // 로그인 종류
                .build();

        boolean saved = userRepository.saveUser(user);

        if (saved) return true;
        else return false;
    }

    // 자신이 등록한 낙서 목록을 조회하는 서비스
    public List<AuthMyVodleGetResDto> getMyVodle(String userCode) {

        // 현재 로그인 사용자 조회
        log.info("등록한 낙서 목록을 조회 시작 : {}", "START");
        UserEntity user = userRepository.findByUserCode(userCode);
        log.info("등록한 낙서 목록을 조회 완료 : {}", user.getId());

        log.info("등록한 낙서 목록 조회 : {}", "... ING ...");

        // 등록한 낙서 목록 조회 결과를 담을 리스트 생성
        List<AuthMyVodleGetResDto> authMyVodleGetResList = new ArrayList<>();

        // 등록한 낙서 목록 결과 담기
        for(VodleEntity vodle: vodleRepository.findByUserId(user.getId())) {
            authMyVodleGetResList.add(
                AuthMyVodleGetResDto.builder()
                    .vodleId(vodle.getId())
                    .address(vodle.getLocation().getAddress())
                    .createdDate(vodle.getCreatedDate())
                    .build()
            );
        }

        log.info("등록한 낙서 개수 : {}", authMyVodleGetResList.size());

        log.info("등록한 낙서 목록 조회 : {}", "... COMPLETE ...");

        return authMyVodleGetResList;
    }

    public void deleteUser(String userId) {
        userRepository.deleteByUserCode(userId);
    }
}