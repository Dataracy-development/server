package com.dataracy.modules.topic.domain.repository;

import com.dataracy.modules.user.domain.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository {
    Long findTopicIdByName(String topicName);
}
