package com.tes.server.domain.vodle.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.tes.server.domain.vodle.entity.type.ContentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class VodleGetResDto {

    // 음성 식별 ID
    private Long vodleId;

    // 작성자
    private String writer;

    // 음성 카테고리
    private ContentType contentType;
    
    // 음성 파일명
    private String fileOriginName;

    // 음성 스트리밍 URL
    private String streamingURL;

    // 주소
    private String address;

    // 위도
    private Double latitude;

    // 경도
    private Double longitude;

    // 등록 시간
    private String createdDate;

    @Builder
    @QueryProjection
    public VodleGetResDto(Long vodleId, String writer, ContentType contentType, String fileOriginName, String streamingURL ,String address, Double latitude, Double longitude, LocalDateTime createdDate) {
        this.vodleId = vodleId;
        this.writer = writer;
        this.contentType = contentType;
        this.fileOriginName = fileOriginName;
        this.streamingURL = streamingURL;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdDate = createdDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));
    }
}