package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.TopicEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.TopicEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.TopicJpaRepository;
import com.dataracy.modules.reference.application.port.out.TopicRepositoryPort;
import com.dataracy.modules.reference.domain.model.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TopicRepositoryAdapter implements TopicRepositoryPort {
    private final TopicJpaRepository topicJpaRepository;

    /**
     * 모든 토픽을 조회하여 도메인 모델 리스트로 반환한다.
     *
     * @return 조회된 모든 토픽의 도메인 객체 리스트
     */
    @Override
    public List<Topic> findAllTopics() {
        List<TopicEntity> topicEntities = topicJpaRepository.findAll();
        return topicEntities.stream()
                .map(TopicEntityMapper::toDomain)
                .toList();
    }

    /**
     * 주어진 ID로 토픽을 조회하여 Optional로 반환합니다.
     *
     * @param topicId 조회할 토픽의 ID. null인 경우 빈 Optional을 반환합니다.
     * @return 토픽이 존재하면 해당 도메인 객체를, 없으면 빈 Optional을 반환합니다.
     */
    @Override
    public Optional<Topic> findTopicById(Long topicId) {
        if (topicId == null) {
            return Optional.empty();
        }
        return topicJpaRepository.findById(topicId)
                .map(TopicEntityMapper::toDomain);
    }

    /**
     * 주어진 토픽 ID에 해당하는 토픽이 데이터베이스에 존재하는지 확인한다.
     *
     * @param topicId 확인할 토픽의 ID
     * @return 토픽이 존재하면 true, 존재하지 않거나 ID가 null이면 false
     */
    @Override
    public boolean existsTopicById(Long topicId) {
        if (topicId == null) {
            return false;
        }
        return topicJpaRepository.existsById(topicId);
    }

    /**
     * 주어진 토픽 ID로 토픽의 라벨을 조회합니다.
     *
     * @param topicId 조회할 토픽의 ID
     * @return 해당 ID의 토픽이 존재하면 라벨을 포함한 Optional, 없으면 빈 Optional
     */
    @Override
    public Optional<String> getLabelById(Long topicId) {
        if (topicId == null) {
            return Optional.empty();
        }
        return topicJpaRepository.findLabelById(topicId);
    }

    /**
     * 주어진 토픽 ID 목록에 해당하는 토픽의 ID와 라벨을 매핑한 Map을 반환합니다.
     *
     * @param topicIds 조회할 토픽의 ID 목록
     * @return 각 토픽 ID와 해당 라벨이 매핑된 Map
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> topicIds) {
        return topicJpaRepository.findAllById(topicIds)
                .stream()
                .collect(Collectors.toMap(TopicEntity::getId, TopicEntity::getLabel));
    }
}
