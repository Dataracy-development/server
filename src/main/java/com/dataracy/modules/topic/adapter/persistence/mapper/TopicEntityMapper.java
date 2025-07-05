package com.dataracy.modules.topic.adapter.persistence.mapper;

import com.dataracy.modules.topic.adapter.persistence.entity.TopicEntity;
import com.dataracy.modules.topic.domain.model.Topic;

/**
 * 토픽 엔티티와 토픽 도메인 모델을 변환하는 매퍼
 */
public class TopicEntityMapper {
    // 토픽 엔티티 -> 토픽 도메인 모델
    public static Topic toDomain(TopicEntity topicEntity) {
        if (topicEntity == null) {
            return null;
        }

        return Topic.toDomain(
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
                topic.getId(),
                topic.getValue(),
                topic.getLabel()
        );
    }
}
