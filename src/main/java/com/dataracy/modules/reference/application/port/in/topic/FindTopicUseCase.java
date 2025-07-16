package com.dataracy.modules.reference.application.port.in.topic;

import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;

/**
 * 토픽 조회 유스케이스
 */
public interface FindTopicUseCase {
    TopicResponse findTopic(Long topicId);
}
