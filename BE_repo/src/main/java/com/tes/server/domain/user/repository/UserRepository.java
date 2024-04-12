package com.tes.server.domain.user.repository;

import com.tes.server.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUserCode(String userCode);
    @Transactional
    void deleteByUserCode(String userCode);

    default boolean saveUser(UserEntity user) {
        try {
            save(user);
            return true; // 저장에 성공한 경우
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 저장에 실패한 경우
        }
    }
}
