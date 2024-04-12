package com.tes.server.domain.vodle.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class VodleDownloadResDto {

    // 다운로드 파일명
    private String downloadSoundName;
    
    // 바이트 배열 파일
    private byte[] downloadSoundBytes;

    @Builder
    public VodleDownloadResDto(String downloadSoundName, byte[] downloadSoundBytes) {
        this.downloadSoundName = downloadSoundName;
        this.downloadSoundBytes = downloadSoundBytes;
    }
}
