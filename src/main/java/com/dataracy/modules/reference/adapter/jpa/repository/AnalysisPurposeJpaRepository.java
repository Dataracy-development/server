/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dataracy.modules.reference.adapter.jpa.entity.AnalysisPurposeEntity;

public interface AnalysisPurposeJpaRepository extends JpaRepository<AnalysisPurposeEntity, Long> {
  /**
   * 주어진 ID에 해당하는 분석 목적 엔터티의 라벨을 Optional로 반환합니다.
   *
   * @param id 라벨을 조회할 엔터티의 ID
   * @return 해당 ID의 라벨이 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
   */
  @Query("SELECT a.label FROM AnalysisPurposeEntity a WHERE a.id = :id")
  Optional<String> findLabelById(Long id);
}
