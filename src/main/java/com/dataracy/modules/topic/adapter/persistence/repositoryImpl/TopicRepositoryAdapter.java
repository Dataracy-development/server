package com.dataracy.modules.topic.adapter.persistence.repositoryImpl;

import com.dataracy.modules.topic.adapter.persistence.entity.TopicEntity;
import com.dataracy.modules.topic.adapter.persistence.repository.TopicJpaRepository;
import com.dataracy.modules.topic.application.port.out.TopicRepositoryPort;
import com.dataracy.modules.topic.domain.exception.TopicException;
import com.dataracy.modules.topic.domain.status.TopicErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryAdapter implements TopicRepositoryPort {

    private final TopicJpaRepository topicJpaRepository;

    @Override
    public Long findTopicIdByName(String topicName) {
        TopicEntity topicEntity = topicJpaRepository.findByLabel(topicName)
                .orElseThrow(() -> new TopicException(TopicErrorStatus.NOT_FOUND_TOPIC_NAME));
        return topicEntity.getId();
    }
}
