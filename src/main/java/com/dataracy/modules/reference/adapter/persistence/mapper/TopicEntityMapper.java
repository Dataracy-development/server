package com.dataracy.modules.reference.adapter.persistence.mapper;

import com.dataracy.modules.reference.adapter.persistence.entity.TopicEntity;
import com.dataracy.modules.reference.domain.model.Topic;

/**
 * 토픽 엔티티와 토픽 도메인 모델을 변환하는 매퍼
 */
public class TopicEntityMapper {
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

        return new Topic(
                topicEntity.getId(),
                topicEntity.getValue(),
                topicEntity.getLabel()
        );
    }

    /**
     * 토픽 도메인 모델 객체를 토픽 엔티티로 변환합니다.
     *
     * @param topic 변환할 토픽 도메인 모델 객체
     * @return 변환된 토픽 엔티티 객체, 입력이 {@code null}이면 {@code null} 반환
     */
    public static TopicEntity toEntity(Topic topic) {
        if (topic == null) {
            return null;
        }

        return TopicEntity.toEntity(
                topic.id(),
                topic.value(),
                topic.label()
        );
    }
}
