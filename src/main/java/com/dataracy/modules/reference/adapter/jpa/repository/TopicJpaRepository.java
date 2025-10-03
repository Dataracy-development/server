/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dataracy.modules.reference.adapter.jpa.entity.TopicEntity;

public interface TopicJpaRepository extends JpaRepository<TopicEntity, Long> {
  /**
   * 주어진 ID에 해당하는 TopicEntity의 라벨을 Optional로 반환합니다.
   *
   * @param id 조회할 TopicEntity의 ID
   * @return 해당 ID의 TopicEntity가 존재하면 라벨을 포함한 Optional, 없으면 빈 Optional
   */
  @Query("SELECT a.label FROM TopicEntity a WHERE a.id = :id")
  Optional<String> findLabelById(Long id);
}
