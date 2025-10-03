/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.jpa.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.jpa.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.AnalysisPurposeEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.AnalysisPurposeJpaRepository;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposePort;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnalysisPurposeDbAdapter implements AnalysisPurposePort {
  private final AnalysisPurposeJpaRepository analysisPurposeJpaRepository;

  // Entity 상수 정의
  private static final String ANALYSIS_PURPOSE_ENTITY = "AnalysisPurposeEntity";

  /**
   * 데이터베이스에 저장된 모든 분석 목적(AnalysisPurpose) 도메인 객체의 목록을 반환한다.
   *
   * @return 모든 분석 목적의 도메인 객체 리스트
   */
  @Override
  public List<AnalysisPurpose> findAllAnalysisPurposes() {
    Instant startTime =
        LoggerFactory.db().logQueryStart(ANALYSIS_PURPOSE_ENTITY, "[findAll] 분석 목적 목록 조회 시작");

    List<AnalysisPurposeEntity> analysisPurposeEntities = analysisPurposeJpaRepository.findAll();
    List<AnalysisPurpose> analysisPurposes =
        analysisPurposeEntities.stream().map(AnalysisPurposeEntityMapper::toDomain).toList();
    LoggerFactory.db().logQueryEnd(ANALYSIS_PURPOSE_ENTITY, "[findAll] 분석 목적 목록 조회 종료", startTime);
    return analysisPurposes;
  }

  /**
   * 주어진 ID에 해당하는 분석 목적 도메인 객체를 Optional로 반환한다.
   *
   * @param analysisPurposeId 조회할 분석 목적의 ID. null이면 Optional.empty()를 반환한다.
   * @return 해당 ID의 분석 목적이 존재하면 Optional로 감싸서 반환하며, 존재하지 않으면 Optional.empty()를 반환한다.
   */
  @Override
  public Optional<AnalysisPurpose> findAnalysisPurposeById(Long analysisPurposeId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                ANALYSIS_PURPOSE_ENTITY,
                "[findById] 분석 목적 조회 시작 analysisPurposeId=" + analysisPurposeId);
    if (analysisPurposeId == null) {
      return Optional.empty();
    }
    Optional<AnalysisPurpose> analysisPurpose =
        analysisPurposeJpaRepository
            .findById(analysisPurposeId)
            .map(AnalysisPurposeEntityMapper::toDomain);
    LoggerFactory.db()
        .logQueryEnd(
            ANALYSIS_PURPOSE_ENTITY,
            "[findById] 분석 목적 조회 종료 analysisPurposeId=" + analysisPurposeId,
            startTime);
    return analysisPurpose;
  }

  /**
   * 주어진 ID에 해당하는 분석 목적이 데이터베이스에 존재하는지 반환합니다.
   *
   * @param analysisPurposeId 존재 여부를 확인할 분석 목적의 ID
   * @return 해당 ID가 null이 아니면 데이터베이스에 존재 여부, null이면 false
   */
  @Override
  public boolean existsAnalysisPurposeById(Long analysisPurposeId) {
    if (analysisPurposeId == null) {
      return false;
    }
    boolean isExists = analysisPurposeJpaRepository.existsById(analysisPurposeId);
    LoggerFactory.db()
        .logExist(
            ANALYSIS_PURPOSE_ENTITY,
            "[existsById] 분석 목적 존재 유무 확인 analysisPurposeId="
                + analysisPurposeId
                + ", isExists="
                + isExists);
    return isExists;
  }

  /**
   * 주어진 ID에 해당하는 분석 목적의 라벨을 Optional로 반환합니다.
   *
   * @param analysisPurposeId 조회할 분석 목적의 ID
   * @return 존재할 경우 라벨을 포함한 Optional, 존재하지 않거나 ID가 null인 경우 빈 Optional
   */
  @Override
  public Optional<String> getLabelById(Long analysisPurposeId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                ANALYSIS_PURPOSE_ENTITY,
                "[findLabelById] 분석 목적 라벨 조회 시작 analysisPurposeId=" + analysisPurposeId);
    if (analysisPurposeId == null) {
      return Optional.empty();
    }
    Optional<String> label = analysisPurposeJpaRepository.findLabelById(analysisPurposeId);
    LoggerFactory.db()
        .logQueryEnd(
            ANALYSIS_PURPOSE_ENTITY,
            "[findLabelById] 분석 목적 라벨 조회 종료 analysisPurposeId="
                + analysisPurposeId
                + ", label="
                + label,
            startTime);
    return label;
  }

  /**
   * 주어진 분석 목적 ID 목록에 해당하는 엔티티의 ID와 라벨을 매핑한 Map을 반환합니다.
   *
   * @param analysisPurposeIds 조회할 분석 목적 ID 목록
   * @return 각 ID에 해당하는 라벨 문자열의 Map
   */
  @Override
  public Map<Long, String> getLabelsByIds(List<Long> analysisPurposeIds) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(ANALYSIS_PURPOSE_ENTITY, "[findAllById] 분석 목적 라벨 목록 조회 시작");
    Map<Long, String> labels =
        analysisPurposeJpaRepository.findAllById(analysisPurposeIds).stream()
            .collect(
                Collectors.toMap(AnalysisPurposeEntity::getId, AnalysisPurposeEntity::getLabel));
    LoggerFactory.db()
        .logQueryEnd(ANALYSIS_PURPOSE_ENTITY, "[findAllById] 분석 목적 라벨 목록 조회 종료", startTime);
    return labels;
  }
}
