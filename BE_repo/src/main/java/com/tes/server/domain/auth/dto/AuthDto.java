package com.tes.server.domain.auth.dto;

import com.tes.server.domain.user.entity.type.OauthProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDto {
    private String userCode;
    private OauthProvider provider;
    private String signature;
}