package com.dataracy.modules.reference.adapter.persistence.impl;

import com.dataracy.modules.reference.adapter.persistence.entity.TopicEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.TopicEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.TopicJpaRepository;
import com.dataracy.modules.reference.application.port.out.TopicRepositoryPort;
import com.dataracy.modules.reference.domain.model.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryAdapter implements TopicRepositoryPort {
    private final TopicJpaRepository topicJpaRepository;

    /**
     * 토픽 엔티티의 모든 데이터셋을 조회한다.
     * @return 토픽 데이터셋
     */
    @Override
    public List<Topic> findAllTopics() {
        List<TopicEntity> topicEntities = topicJpaRepository.findAll();
        return topicEntities.stream()
                .map(TopicEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Topic> findTopicById(Long topicId) {
        return topicJpaRepository.findById(topicId)
                .map(TopicEntityMapper::toDomain);
    }

    /**
     * 토픽 아이디로 db에 해당하는 토픽이 존재하는지 유무를 확인한다.
     * @param topicId 토픽 id
     * @return 존재 유무
     */
    @Override
    public Boolean existsTopicById(Long topicId) {
        return topicJpaRepository.existsById(topicId);
    }
}
