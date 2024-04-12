package com.tes.server.domain.user.entity;

import com.tes.server.domain.vodle.entity.VodleEntity;
import com.tes.server.domain.user.entity.type.OauthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "userCode", "oauthProvider"})
public class UserEntity {

    // 기본 키 생성을 DB에 위임하는 전략
    @Column(name = "user_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 코드
    @Column(name = "user_code", nullable = false, unique = true)
    private String userCode;

    // 로그인 종류
    @Column(name = "oauth_provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    // 유저 낙서 목록
    @OneToMany(mappedBy = "user")
    private List<VodleEntity> soundRecords = new ArrayList<>();

    // 생성날짜
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Builder
    public UserEntity(String userCode, OauthProvider oauthProvider) {
        this.userCode = userCode;
        this.oauthProvider = oauthProvider;
        this.createdDate = LocalDateTime.now();
    }
}