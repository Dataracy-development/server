package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.TopicEntity;
import com.dataracy.modules.reference.domain.model.Topic;

/** 토픽 엔티티와 토픽 도메인 모델을 변환하는 매퍼 */
public final class TopicEntityMapper {
  /** 인스턴스 생성을 방지하기 위한 private 생성자입니다. */
  private TopicEntityMapper() {}

  /**
   * TopicEntity 객체를 Topic 도메인 모델로 변환합니다.
   *
   * @param topicEntity 변환할 TopicEntity 객체
   * @return 변환된 Topic 도메인 모델 객체, 입력이 null이면 null 반환
   */
  public static Topic toDomain(TopicEntity topicEntity) {
    if (topicEntity == null) {
      return null;
    }

    return new Topic(topicEntity.getId(), topicEntity.getValue(), topicEntity.getLabel());
  }

  /**
   * Topic 도메인 모델 객체를 TopicEntity로 변환합니다.
   *
   * @param topic 변환할 Topic 도메인 모델 객체
   * @return 변환된 TopicEntity 객체, 입력이 null이면 null을 반환합니다.
   */
  public static TopicEntity toEntity(Topic topic) {
    if (topic == null) {
      return null;
    }

    return TopicEntity.of(topic.value(), topic.label());
  }
}
