package com.dataracy.modules.topic.adapter.persistence.repositoryImpl;

import com.dataracy.modules.topic.adapter.persistence.entity.TopicEntity;
import com.dataracy.modules.topic.adapter.persistence.mapper.TopicEntityMapper;
import com.dataracy.modules.topic.adapter.persistence.repository.TopicJpaRepository;
import com.dataracy.modules.topic.application.port.out.TopicRepositoryPort;
import com.dataracy.modules.topic.domain.exception.TopicException;
import com.dataracy.modules.topic.domain.model.Topic;
import com.dataracy.modules.topic.domain.status.TopicErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryAdapter implements TopicRepositoryPort {

    private final TopicJpaRepository topicJpaRepository;

    /**
     * 토픽명으로 해당하는 토픽의 id를 반환한다.
     * @param topicName 토픽명
     * @return 토픽 id
     */
    @Override
    public Long findTopicIdByName(String topicName) {
        TopicEntity topicEntity = topicJpaRepository.findByLabel(topicName)
                .orElseThrow(() -> new TopicException(TopicErrorStatus.NOT_FOUND_TOPIC_NAME));
        return topicEntity.getId();
    }

    /**
     * 토픽 엔티티의 모든 데이터셋을 조회한다.
     * @return 토픽 데이터셋
     */
    @Override
    public List<Topic> allTopics() {
        List<TopicEntity> topicEntities = topicJpaRepository.findAll();
        return topicEntities.stream()
                .map(TopicEntityMapper::toDomain)
                .toList();
    }
}
