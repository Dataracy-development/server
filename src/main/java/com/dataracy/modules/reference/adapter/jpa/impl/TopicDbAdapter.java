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
import com.dataracy.modules.reference.adapter.jpa.entity.TopicEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.TopicEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.TopicJpaRepository;
import com.dataracy.modules.reference.application.port.out.TopicPort;
import com.dataracy.modules.reference.domain.model.Topic;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TopicDbAdapter implements TopicPort {
  private final TopicJpaRepository topicJpaRepository;

  // Entity 상수 정의
  private static final String TOPIC_ENTITY = "TopicEntity";

  /**
   * 모든 토픽을 조회하여 도메인 `Topic` 객체의 리스트로 반환한다.
   *
   * @return 데이터베이스에 저장된 모든 토픽의 도메인 객체 리스트
   */
  @Override
  public List<Topic> findAllTopics() {
    Instant startTime = LoggerFactory.db().logQueryStart(TOPIC_ENTITY, "[findAll] 토픽 목록 조회 시작");
    List<TopicEntity> topicEntities = topicJpaRepository.findAll();
    List<Topic> topics = topicEntities.stream().map(TopicEntityMapper::toDomain).toList();
    LoggerFactory.db().logQueryEnd(TOPIC_ENTITY, "[findAll] 토픽 목록 조회 종료", startTime);
    return topics;
  }

  /**
   * 주어진 ID에 해당하는 토픽을 조회하여 Optional로 반환합니다.
   *
   * @param topicId 조회할 토픽의 ID. null이면 빈 Optional을 반환합니다.
   * @return 토픽이 존재하면 도메인 객체를 포함한 Optional, 존재하지 않거나 ID가 null이면 빈 Optional을 반환합니다.
   */
  @Override
  public Optional<Topic> findTopicById(Long topicId) {
    Instant startTime =
        LoggerFactory.db().logQueryStart(TOPIC_ENTITY, "[findById] 토픽 목록 조회 시작 topicId=" + topicId);
    if (topicId == null) {
      return Optional.empty();
    }
    Optional<Topic> topic = topicJpaRepository.findById(topicId).map(TopicEntityMapper::toDomain);
    LoggerFactory.db()
        .logQueryEnd(TOPIC_ENTITY, "[findById] 토픽 목록 조회 종료 topicId=" + topicId, startTime);
    return topic;
  }

  /**
   * 주어진 토픽 ID가 데이터베이스에 존재하는지 여부를 반환한다.
   *
   * @param topicId 존재 여부를 확인할 토픽의 ID
   * @return 토픽이 존재하면 true, topicId가 null이거나 존재하지 않으면 false
   */
  @Override
  public boolean existsTopicById(Long topicId) {
    if (topicId == null) {
      return false;
    }
    boolean isExists = topicJpaRepository.existsById(topicId);
    LoggerFactory.db()
        .logExist(
            TOPIC_ENTITY, "[existsById] 토픽 존재 유무 확인 topicId=" + topicId + ", isExists=" + isExists);
    return isExists;
  }

  /**
   * 주어진 토픽 ID에 해당하는 토픽의 라벨을 Optional로 반환합니다.
   *
   * @param topicId 조회할 토픽의 ID
   * @return 토픽이 존재하면 라벨을 포함한 Optional, 존재하지 않거나 ID가 null이면 빈 Optional
   */
  @Override
  public Optional<String> getLabelById(Long topicId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(TOPIC_ENTITY, "[findLabelById] 토픽 라벨 조회 시작 topicId=" + topicId);
    if (topicId == null) {
      return Optional.empty();
    }
    Optional<String> label = topicJpaRepository.findLabelById(topicId);
    LoggerFactory.db()
        .logQueryEnd(
            TOPIC_ENTITY,
            "[findLabelById] 토픽 라벨 조회 종료 topicId=" + topicId + ", label=" + label,
            startTime);
    return label;
  }

  /**
   * 주어진 토픽 ID 목록에 해당하는 토픽의 ID와 라벨을 매핑한 Map을 반환합니다.
   *
   * @param topicIds 조회할 토픽의 ID 목록
   * @return 각 토픽 ID와 해당 라벨이 매핑된 Map
   */
  @Override
  public Map<Long, String> getLabelsByIds(List<Long> topicIds) {
    Instant startTime =
        LoggerFactory.db().logQueryStart(TOPIC_ENTITY, "[findAllById] 토픽 라벨 목록 조회 시작");
    Map<Long, String> labels =
        topicJpaRepository.findAllById(topicIds).stream()
            .collect(Collectors.toMap(TopicEntity::getId, TopicEntity::getLabel));
    LoggerFactory.db().logQueryEnd(TOPIC_ENTITY, "[findAllById] 토픽 라벨 목록 조회 종료", startTime);
    return labels;
  }
}
