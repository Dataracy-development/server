package com.dataracy.modules.topic.application.port.out;

import org.springframework.stereotype.Repository;

/**
 * 토픽 db에 접근하는 포트
 */
@Repository
public interface TopicRepositoryPort {
    Long findTopicIdByName(String topicName);
}
