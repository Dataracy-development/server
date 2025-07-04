package com.dataracy.modules.topic.adapter.persistence.repository;

import com.dataracy.modules.topic.adapter.persistence.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicJpaRepository extends JpaRepository<TopicEntity, Long> {
    Optional<TopicEntity> findByName(String topicName);
}
