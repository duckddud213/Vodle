package com.tes.server.domain.vodle.dto.request;

import com.tes.server.domain.vodle.entity.type.RecordType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VodleCreateReqDto {

    // 작성자
    private String writer;

    // 기록 종류
    private RecordType recordType;

    // 변조된 음성 스트리밍 URL
    private String streamingURL;
    
    // 위도
    private Double longitude;

    // 경도
    private Double latitude;
}
