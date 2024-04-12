package com.tes.server.domain.user.dto;

import com.tes.server.domain.user.entity.type.OauthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private String userCode;
    private OauthProvider provider;
}