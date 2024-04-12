package com.tes.server.domain.vodle.repository;

import com.tes.server.domain.vodle.dto.request.VodleLocationReqDto;
import com.tes.server.domain.vodle.dto.response.VodleGetResDto;

import java.util.List;

public interface VodleRepositoryCustom {

    // 범위 내 음성 낙서 리스트 조회
    List<VodleGetResDto> findByLocation(VodleLocationReqDto vodleLocationReqDto);
}