package com.tes.server.domain.vodle.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VodleTtsReqDto {

    // 음성 선택
    @JsonProperty("selected_voice")
    private String selectedVoice;

    // 음성으로 변환 할 텍스트
    private String content;

    @Builder
    public VodleTtsReqDto(String selectedVoice, String content) {
        this.selectedVoice = selectedVoice;
        this.content = content;
    }
}
