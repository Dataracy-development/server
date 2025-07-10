package com.dataracy.modules.reference.adapter.persistence.repository;

import com.dataracy.modules.reference.adapter.persistence.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicJpaRepository extends JpaRepository<TopicEntity, Long> {
}
