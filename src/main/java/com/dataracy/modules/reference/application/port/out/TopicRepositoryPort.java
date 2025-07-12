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
     * 데이터베이스에 저장된 모든 도메인을 반환합니다.
     *
     * @return 모든 도메인의 리스트
     */
    List<Topic> allTopics();

    /**
     * 주어진 ID에 해당하는 Topic이 존재하는지 유무를 반환합니다.
     *
     * @param topicId 조회할 도메인의 고유 식별자
     * @return 해당 ID의 Occupation 존재 유무
     */
    Boolean isExistTopicById(Long topicId);
}
