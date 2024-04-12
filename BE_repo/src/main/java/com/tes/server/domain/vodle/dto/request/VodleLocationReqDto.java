package com.tes.server.domain.vodle.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
public class VodleLocationReqDto {

    // 중앙 위도
    private Double centerLatitude;

    // 중앙 경도
    private Double centerLongitude;
    
    // 우상단 위도
    private Double northEastLatitude;

    // 우상단 경도
    private Double northEastLongitude;

    // 좌하단 위도
    private Double southWestLatitude;

    // 좌하단 경도
    private Double southWestLongitude;
}
