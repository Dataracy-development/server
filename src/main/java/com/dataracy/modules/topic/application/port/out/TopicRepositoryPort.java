package com.dataracy.modules.topic.application.port.out;

import com.dataracy.modules.topic.domain.model.Topic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 토픽 db에 접근하는 포트
 */
@Repository
public interface TopicRepositoryPort {
    List<Topic> allTopics();
    Boolean isExistTopicById(Long topicId);
}
