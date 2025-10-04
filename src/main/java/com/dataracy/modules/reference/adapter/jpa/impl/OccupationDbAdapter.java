package com.dataracy.modules.reference.adapter.jpa.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.jpa.entity.OccupationEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.OccupationEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.OccupationJpaRepository;
import com.dataracy.modules.reference.application.port.out.OccupationPort;
import com.dataracy.modules.reference.domain.model.Occupation;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OccupationDbAdapter implements OccupationPort {
  private final OccupationJpaRepository occupationJpaRepository;

  // Entity 상수 정의
  private static final String OCCUPATION_ENTITY = "OccupationEntity";

  /**
   * 모든 직업(occupation) 데이터를 조회하여 도메인 객체 리스트로 반환한다.
   *
   * @return 전체 직업 정보를 담은 Occupation 객체 리스트
   */
  @Override
  public List<Occupation> findAllOccupations() {
    Instant startTime =
        LoggerFactory.db().logQueryStart(OCCUPATION_ENTITY, "[findAll] 직업 목록 조회 시작");
    List<OccupationEntity> occupationEntities = occupationJpaRepository.findAll();
    List<Occupation> occupations =
        occupationEntities.stream().map(OccupationEntityMapper::toDomain).toList();
    LoggerFactory.db().logQueryEnd(OCCUPATION_ENTITY, "[findAll] 직업 목록 조회 종료", startTime);
    return occupations;
  }

  /**
   * 주어진 ID에 해당하는 직업 정보를 조회하여 Optional로 반환한다.
   *
   * @param occupationId 조회할 직업의 ID. null이면 Optional.empty()를 반환한다.
   * @return 해당 ID의 직업이 존재하면 Optional로 감싼 도메인 객체, 존재하지 않거나 ID가 null이면 Optional.empty()
   */
  @Override
  public Optional<Occupation> findOccupationById(Long occupationId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                OCCUPATION_ENTITY, "[findById] 직업 목록 조회 시작 occupationId=" + occupationId);
    if (occupationId == null) {
      return Optional.empty();
    }
    Optional<Occupation> occupation =
        occupationJpaRepository.findById(occupationId).map(OccupationEntityMapper::toDomain);
    LoggerFactory.db()
        .logQueryEnd(
            OCCUPATION_ENTITY, "[findById] 직업 목록 조회 종료 occupationId=" + occupationId, startTime);
    return occupation;
  }

  /**
   * 주어진 직업 ID에 해당하는 직업이 존재하는지 확인합니다.
   *
   * @param occupationId 존재 여부를 확인할 직업의 ID
   * @return 직업이 존재하면 true, occupationId가 null이거나 존재하지 않으면 false
   */
  @Override
  public boolean existsOccupationById(Long occupationId) {
    if (occupationId == null) {
      return false;
    }
    boolean isExists = occupationJpaRepository.existsById(occupationId);
    LoggerFactory.db()
        .logExist(
            OCCUPATION_ENTITY,
            "[existsById] 직업 존재 유무 확인 occupationId=" + occupationId + ", isExists=" + isExists);
    return isExists;
  }

  /**
   * 주어진 ID에 해당하는 직업의 라벨을 Optional로 반환합니다.
   *
   * @param occupationId 조회할 직업의 ID
   * @return 직업 라벨이 존재하면 Optional로 반환하며, ID가 null이거나 라벨이 없으면 Optional.empty()를 반환합니다.
   */
  @Override
  public Optional<String> getLabelById(Long occupationId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                OCCUPATION_ENTITY, "[findLabelById] 직업 라벨 조회 시작 occupationId=" + occupationId);
    if (occupationId == null) {
      return Optional.empty();
    }
    Optional<String> label = occupationJpaRepository.findLabelById(occupationId);
    LoggerFactory.db()
        .logQueryEnd(
            OCCUPATION_ENTITY,
            "[findLabelById] 직업 라벨 조회 종료 occupationId=" + occupationId + ", label=" + label,
            startTime);
    return label;
  }

  /**
   * 주어진 직업 ID 목록에 해당하는 직업의 ID와 라벨을 매핑한 Map을 반환합니다.
   *
   * @param occupationIds 라벨을 조회할 직업 ID 목록
   * @return 각 직업 ID와 해당 라벨이 매핑된 Map 객체
   */
  @Override
  public Map<Long, String> getLabelsByIds(List<Long> occupationIds) {
    Instant startTime =
        LoggerFactory.db().logQueryStart(OCCUPATION_ENTITY, "[findAllById] 직업 라벨 목록 조회 시작");
    Map<Long, String> labels =
        occupationJpaRepository.findAllById(occupationIds).stream()
            .collect(Collectors.toMap(OccupationEntity::getId, OccupationEntity::getLabel));
    LoggerFactory.db().logQueryEnd(OCCUPATION_ENTITY, "[findAllById] 직업 라벨 목록 조회 종료", startTime);
    return labels;
  }
}
