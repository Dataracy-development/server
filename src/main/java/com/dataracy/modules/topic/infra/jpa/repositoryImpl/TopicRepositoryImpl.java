package com.dataracy.modules.topic.infra.jpa.repositoryImpl;

import com.dataracy.modules.topic.domain.repository.TopicRepository;
import com.dataracy.modules.topic.infra.jpa.entity.TopicEntity;
import com.dataracy.modules.topic.infra.jpa.repository.TopicJpaRepository;
import com.dataracy.modules.topic.status.TopicErrorStatus;
import com.dataracy.modules.topic.status.TopicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryImpl implements TopicRepository {

    private final TopicJpaRepository topicJpaRepository;

    @Override
    public Long findTopicIdByName(String topicName) {
        TopicEntity topicEntity = topicJpaRepository.findByName(topicName)
                .orElseThrow(() -> new TopicException(TopicErrorStatus.NOT_FOUND_TOPIC_NAME));
        return topicEntity.getId();
    }
}
