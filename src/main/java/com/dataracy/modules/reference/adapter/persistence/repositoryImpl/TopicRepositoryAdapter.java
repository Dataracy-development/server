package com.dataracy.modules.reference.adapter.persistence.repositoryImpl;

import com.dataracy.modules.reference.adapter.persistence.entity.TopicEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.TopicEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.TopicJpaRepository;
import com.dataracy.modules.reference.application.port.out.TopicRepositoryPort;
import com.dataracy.modules.reference.domain.model.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryAdapter implements TopicRepositoryPort {
    private final TopicJpaRepository topicJpaRepository;

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

    /**
     * 토픽 아이디로 db에 해당하는 토픽이 존재하는지 유무를 확인한다.
     * @param topicId 토픽 id
     * @return 존재 유무
     */
    @Override
    public Boolean isExistTopicById(Long topicId) {
        return topicJpaRepository.existsById(topicId);
    }
}
