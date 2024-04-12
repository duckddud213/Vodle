package com.tes.server.domain.vodle.repository;

import com.tes.server.domain.vodle.entity.PitchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PitchRepository extends JpaRepository<PitchEntity, Long> {
    PitchEntity findByVoiceType(String VoiceType);
}
