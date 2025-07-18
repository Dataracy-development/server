package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.Topic;

import java.util.List;
import java.util.Optional;

/**
 * 토픽 db에 접근하는 포트
 */
public interface TopicRepositoryPort {
    /**
 * 데이터베이스에 저장된 모든 토픽을 조회하여 리스트로 반환합니다.
 *
 * @return 모든 토픽 객체의 리스트
 */
    List<Topic> findAllTopics();

    /**
 * 주어진 ID에 해당하는 토픽을 조회합니다.
 *
 * @param topicId 조회할 토픽의 고유 식별자
 * @return 토픽이 존재하면 해당 Topic 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
 */
Optional<Topic> findTopicById(Long topicId);

    /**
 * 주어진 ID를 가진 Topic이 데이터베이스에 존재하는지 확인합니다.
 *
 * @param topicId 존재 여부를 확인할 Topic의 고유 식별자
 * @return Topic이 존재하면 true, 존재하지 않으면 false
 */
    boolean existsTopicById(Long topicId);

    Optional<String> getLabelById(Long topicId);
}
