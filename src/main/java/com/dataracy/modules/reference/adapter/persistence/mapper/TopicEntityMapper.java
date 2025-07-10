package com.dataracy.modules.reference.adapter.persistence.mapper;

import com.dataracy.modules.reference.adapter.persistence.entity.TopicEntity;
import com.dataracy.modules.reference.domain.model.Topic;

/**
 * 토픽 엔티티와 토픽 도메인 모델을 변환하는 매퍼
 */
public class TopicEntityMapper {
    // 토픽 엔티티 -> 토픽 도메인 모델
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

    // 토픽 도메인 모델 -> 토픽 엔티티
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
