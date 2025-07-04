package com.dataracy.modules.topic.adapter.persistence.mapper;

import com.dataracy.modules.topic.adapter.persistence.entity.TopicEntity;
import com.dataracy.modules.topic.domain.model.Topic;

/**
 * 토픽 도메인 모델과 토픽 엔티티를 변환하는 매퍼
 */
public class TopicMapper {
    public static Topic toDomain(TopicEntity topicEntity) {
        return Topic.toDomain(
                topicEntity.getId(),
                topicEntity.getDomain(),
                topicEntity.getName()
        );
    }

    public static TopicEntity toEntity(Topic topic) {
        return TopicEntity.toEntity(
                topic.getId(),
                topic.getDomain(),
                topic.getName()
        );
    }
}
