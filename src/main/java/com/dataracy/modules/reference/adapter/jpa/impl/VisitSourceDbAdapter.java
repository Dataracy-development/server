package com.dataracy.modules.reference.adapter.jpa.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.jpa.entity.VisitSourceEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.VisitSourceEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.VisitSourceJpaRepository;
import com.dataracy.modules.reference.application.port.out.VisitSourcePort;
import com.dataracy.modules.reference.domain.model.VisitSource;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VisitSourceDbAdapter implements VisitSourcePort {
  private final VisitSourceJpaRepository visitSourceJpaRepository;

  // Entity 상수 정의
  private static final String VISIT_SOURCE_ENTITY = "VisitSourceEntity";

  /**
   * 모든 방문 경로(VisitSource) 데이터를 조회하여 리스트로 반환한다.
   *
   * @return 데이터베이스에 저장된 모든 방문 경로의 도메인 객체 리스트
   */
  @Override
  public List<VisitSource> findAllVisitSources() {
    Instant startTime =
        LoggerFactory.db().logQueryStart(VISIT_SOURCE_ENTITY, "[findAll] 방문 경로 목록 조회 시작");
    List<VisitSourceEntity> visitSourceEntities = visitSourceJpaRepository.findAll();
    List<VisitSource> visitSources =
        visitSourceEntities.stream().map(VisitSourceEntityMapper::toDomain).toList();
    LoggerFactory.db().logQueryEnd(VISIT_SOURCE_ENTITY, "[findAll] 방문 경로 목록 조회 종료", startTime);
    return visitSources;
  }

  /**
   * 주어진 ID에 해당하는 방문 경로 도메인 객체를 Optional로 반환한다.
   *
   * @param visitSourceId 조회할 방문 경로의 ID. null이면 빈 Optional을 반환한다.
   * @return 해당 ID의 방문 경로가 존재하면 도메인 객체를 포함한 Optional, 존재하지 않거나 ID가 null이면 빈 Optional
   */
  @Override
  public Optional<VisitSource> findVisitSourceById(Long visitSourceId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                VISIT_SOURCE_ENTITY, "[findById] 방문 경로 목록 조회 시작 visitSourceId=" + visitSourceId);
    if (visitSourceId == null) {
      return Optional.empty();
    }
    Optional<VisitSource> visitSource =
        visitSourceJpaRepository.findById(visitSourceId).map(VisitSourceEntityMapper::toDomain);
    LoggerFactory.db()
        .logQueryEnd(
            VISIT_SOURCE_ENTITY,
            "[findById] 방문 경로 목록 조회 종료 visitSourceId=" + visitSourceId,
            startTime);
    return visitSource;
  }

  /**
   * 주어진 ID의 방문 소스가 데이터베이스에 존재하는지 확인합니다.
   *
   * @param visitSourceId 존재 여부를 확인할 방문 소스의 ID
   * @return 방문 소스가 존재하면 true, ID가 null이거나 존재하지 않으면 false
   */
  @Override
  public boolean existsVisitSourceById(Long visitSourceId) {
    if (visitSourceId == null) {
      return false;
    }
    boolean isExists = visitSourceJpaRepository.existsById(visitSourceId);
    LoggerFactory.db()
        .logExist(
            VISIT_SOURCE_ENTITY,
            "[existsById] 방문 경로 존재 유무 확인 visitSourceId="
                + visitSourceId
                + ", isExists="
                + isExists);
    return isExists;
  }

  /**
   * 주어진 ID에 해당하는 방문 소스의 라벨을 Optional로 반환합니다.
   *
   * @param visitSourceId 조회할 방문 소스의 ID
   * @return 방문 소스가 존재하면 해당 라벨을, 존재하지 않거나 ID가 null이면 빈 Optional을 반환합니다.
   */
  @Override
  public Optional<String> getLabelById(Long visitSourceId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                VISIT_SOURCE_ENTITY,
                "[findLabelById] 방문 경로 라벨 조회 시작 visitSourceId=" + visitSourceId);
    if (visitSourceId == null) {
      return Optional.empty();
    }
    Optional<String> label = visitSourceJpaRepository.findLabelById(visitSourceId);
    LoggerFactory.db()
        .logQueryEnd(
            VISIT_SOURCE_ENTITY,
            "[findLabelById] 방문 경로 라벨 조회 종료 visitSourceId=" + visitSourceId + ", label=" + label,
            startTime);
    return label;
  }

  /**
   * 주어진 방문 소스 ID 목록에 대해 각 ID와 해당 라벨을 매핑한 Map을 반환합니다.
   *
   * @param visitSourceIds 라벨을 조회할 방문 소스 ID 목록
   * @return 각 ID에 해당하는 라벨의 매핑 Map
   */
  @Override
  public Map<Long, String> getLabelsByIds(List<Long> visitSourceIds) {
    Instant startTime =
        LoggerFactory.db().logQueryStart(VISIT_SOURCE_ENTITY, "[findAllById] 방문 경로 라벨 목록 조회 시작");
    Map<Long, String> labels =
        visitSourceJpaRepository.findAllById(visitSourceIds).stream()
            .collect(Collectors.toMap(VisitSourceEntity::getId, VisitSourceEntity::getLabel));
    LoggerFactory.db()
        .logQueryEnd(VISIT_SOURCE_ENTITY, "[findAllById] 방문 경로 라벨 목록 조회 종료", startTime);
    return labels;
  }
}
