package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.Topic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 토픽 db에 접근하는 포트
 */
@Repository
public interface TopicRepositoryPort {
    /**
 * 데이터베이스에 저장된 모든 토픽을 반환합니다.
 *
 * @return 모든 토픽의 리스트
 */
    List<Topic> allTopics();

    /**
 * 주어진 ID에 해당하는 Topic이 데이터베이스에 존재하는지 여부를 반환합니다.
 *
 * @param topicId 확인할 Topic의 고유 식별자
 * @return Topic이 존재하면 true, 존재하지 않으면 false
 */
    Boolean isExistTopicById(Long topicId);
}
