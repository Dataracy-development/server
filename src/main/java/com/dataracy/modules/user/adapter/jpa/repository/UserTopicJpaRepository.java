package com.dataracy.modules.user.adapter.jpa.repository;

import com.dataracy.modules.user.adapter.jpa.entity.UserTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTopicJpaRepository extends JpaRepository<UserTopicEntity, Long> {
    void deleteAllByUserId(Long userId);
}
