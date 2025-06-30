package com.dataracy.modules.topic.infra.mapper;

import com.dataracy.modules.topic.domain.model.Topic;
import com.dataracy.modules.topic.infra.jpa.entity.TopicEntity;

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
