package com.tes.server.domain.vodle.entity.type;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Embeddable
@NoArgsConstructor
public class Location {

    // 주소
    @Column(name = "address", nullable = false)
    private String address;

    // 위도
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    // 경도
    @Column(name = "longitude", nullable = false)
    private Double longitude;
    
    // 생성자
    public Location(String address, Double latitude, Double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}