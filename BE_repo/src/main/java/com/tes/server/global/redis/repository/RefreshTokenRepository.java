package com.tes.server.global.redis.repository;


import com.tes.server.global.redis.dto.RefreshTokenDto;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenDto, String> {
    RefreshTokenDto findByUserCode(String userCode);
}