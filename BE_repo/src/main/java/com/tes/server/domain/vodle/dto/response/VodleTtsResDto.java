package com.tes.server.domain.vodle.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
public class VodleTtsResDto {

    private String convertedFileUrl;

    private String selectedVoice;


    @Builder
    public VodleTtsResDto(String convertedFileUrl, String selectedVoice) {
        this.convertedFileUrl = convertedFileUrl;
        this.selectedVoice = selectedVoice;
    }
}
