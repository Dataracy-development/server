package com.dataracy.modules.topic.application;

import com.dataracy.modules.topic.domain.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicQueryService {

    private final TopicRepository topicRepository;

    /**
     * 도메인 토픽명으로 db에서 토픽id를 찾아 반환한다.
     * @param topicName 도메인 토픽명
     * @return 토픽id
     */
    public Long findTopicIdByName(String topicName) {
        return topicRepository.findTopicIdByName(topicName);
    }
}
