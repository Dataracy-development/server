package com.dataracy.modules.reference.adapter.jpa.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.jpa.entity.AuthorLevelEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.AuthorLevelEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.AuthorLevelJpaRepository;
import com.dataracy.modules.reference.application.port.out.AuthorLevelPort;
import com.dataracy.modules.reference.domain.model.AuthorLevel;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthorLevelDbAdapter implements AuthorLevelPort {
  private final AuthorLevelJpaRepository authorLevelJpaRepository;

  // Entity 상수 정의
  private static final String AUTHOR_LEVEL_ENTITY = "AuthorLevelEntity";

  /**
   * 모든 작성자 유형(AuthorLevel) 도메인 객체 목록을 조회한다.
   *
   * @return 데이터베이스에 저장된 모든 작성자 유형의 리스트
   */
  @Override
  public List<AuthorLevel> findAllAuthorLevels() {
    Instant startTime =
        LoggerFactory.db().logQueryStart(AUTHOR_LEVEL_ENTITY, "[findAll] 작성자 유형 목록 조회 시작");
    List<AuthorLevelEntity> authorLevelEntities = authorLevelJpaRepository.findAll();
    List<AuthorLevel> authorLevels =
        authorLevelEntities.stream().map(AuthorLevelEntityMapper::toDomain).toList();
    LoggerFactory.db().logQueryEnd(AUTHOR_LEVEL_ENTITY, "[findAll] 작성자 유형 목록 조회 종료", startTime);
    return authorLevels;
  }

  /**
   * 주어진 ID에 해당하는 작성자 유형 도메인 객체를 Optional로 반환한다.
   *
   * @param authorLevelId 조회할 작성자 유형의 ID
   * @return 해당 ID의 작성자 유형이 존재하면 도메인 객체를 포함하는 Optional, 존재하지 않으면 빈 Optional
   */
  @Override
  public Optional<AuthorLevel> findAuthorLevelById(Long authorLevelId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                AUTHOR_LEVEL_ENTITY, "[findById] 작성자 유형 목록 조회 시작 authorLevelId=" + authorLevelId);
    if (authorLevelId == null) {
      return Optional.empty();
    }
    Optional<AuthorLevel> authorLevel =
        authorLevelJpaRepository.findById(authorLevelId).map(AuthorLevelEntityMapper::toDomain);
    LoggerFactory.db()
        .logQueryEnd(
            AUTHOR_LEVEL_ENTITY,
            "[findById] 작성자 유형 목록 조회 종료 authorLevelId=" + authorLevelId,
            startTime);
    return authorLevel;
  }

  /**
   * 주어진 ID에 해당하는 AuthorLevel 엔티티의 존재 여부를 반환합니다.
   *
   * @param authorLevelId 존재 여부를 확인할 AuthorLevel의 ID
   * @return 엔티티가 존재하면 true, ID가 null이거나 존재하지 않으면 false
   */
  @Override
  public boolean existsAuthorLevelById(Long authorLevelId) {
    if (authorLevelId == null) {
      return false;
    }
    boolean isExists = authorLevelJpaRepository.existsById(authorLevelId);
    LoggerFactory.db()
        .logExist(
            AUTHOR_LEVEL_ENTITY,
            "[existsById] 작성자 유형 존재 유무 확인 authorLevelId="
                + authorLevelId
                + ", isExists="
                + isExists);
    return isExists;
  }

  /**
   * 주어진 ID에 해당하는 AuthorLevel의 라벨을 Optional로 반환합니다.
   *
   * @param authorLevelId 조회할 AuthorLevel의 ID
   * @return 해당 ID의 라벨이 존재하면 Optional로 반환하며, ID가 null이거나 라벨이 없으면 빈 Optional을 반환합니다.
   */
  @Override
  public Optional<String> getLabelById(Long authorLevelId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                AUTHOR_LEVEL_ENTITY,
                "[findLabelById] 작성자 유형 라벨 조회 시작 authorLevelId=" + authorLevelId);
    if (authorLevelId == null) {
      return Optional.empty();
    }
    Optional<String> label = authorLevelJpaRepository.findLabelById(authorLevelId);
    LoggerFactory.db()
        .logQueryEnd(
            AUTHOR_LEVEL_ENTITY,
            "[findLabelById] 작성자 유형 라벨 조회 종료 authorLevelId=" + authorLevelId + ", label=" + label,
            startTime);
    return label;
  }

  /**
   * 주어진 ID 목록에 해당하는 작성자 유형의 ID와 라벨을 매핑하여 반환합니다.
   *
   * @param authorLevelIds 라벨을 조회할 작성자 유형 ID 목록
   * @return 각 ID에 해당하는 라벨을 담은 Map 객체
   */
  @Override
  public Map<Long, String> getLabelsByIds(List<Long> authorLevelIds) {
    Instant startTime =
        LoggerFactory.db().logQueryStart(AUTHOR_LEVEL_ENTITY, "[findAllById] 작성자 유형 라벨 목록 조회 시작");
    Map<Long, String> labels =
        authorLevelJpaRepository.findAllById(authorLevelIds).stream()
            .collect(Collectors.toMap(AuthorLevelEntity::getId, AuthorLevelEntity::getLabel));
    LoggerFactory.db()
        .logQueryEnd(AUTHOR_LEVEL_ENTITY, "[findAllById] 작성자 유형 라벨 목록 조회 종료", startTime);
    return labels;
  }
}
