package com.dataracy.modules.user.infra.jpa.repository;

import com.dataracy.modules.user.infra.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByProviderId(String providerId);
    UserEntity findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);

    @Modifying
    @Query("UPDATE UserEntity u SET u.isDeleted = true WHERE u.id = :userId")
    void markUserAsDeleted(Long userId);
}
