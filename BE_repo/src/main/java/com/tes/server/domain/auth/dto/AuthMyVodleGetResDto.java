package com.tes.server.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class AuthMyVodleGetResDto {

    // 음성 식별 ID
    private Long vodleId;

    // 주소
    private String address;

    // 등록 시간
    private String createdDate;

    @Builder
    public AuthMyVodleGetResDto(Long vodleId, String address, LocalDateTime createdDate) {
        this.vodleId = vodleId;
        this.address = address;
        this.createdDate = createdDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));
    }
}
