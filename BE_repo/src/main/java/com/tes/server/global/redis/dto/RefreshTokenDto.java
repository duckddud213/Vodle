package com.tes.server.global.redis.dto;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("token")
public class RefreshTokenDto {

    @Id
    private String userCode;
    private String refreshToken;

    public RefreshTokenDto(String userCode, String refreshToken) {
        this.userCode = userCode;
        this.refreshToken = refreshToken;
    }
}