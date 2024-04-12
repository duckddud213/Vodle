package com.tes.server.domain.vodle.repository;

import com.tes.server.domain.vodle.entity.VodleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VodleRepository extends JpaRepository<VodleEntity, Long>, VodleRepositoryCustom {

    // 회원 식별 ID로 등록한 음성 목록 조회
    List<VodleEntity> findByUserId(Long id);
}
