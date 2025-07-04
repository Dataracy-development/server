package com.dataracy.modules.topic.application.service.query;

import com.dataracy.modules.topic.application.port.in.FindTopicUseCase;
import com.dataracy.modules.topic.application.port.out.TopicRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicQueryService implements FindTopicUseCase {

    private final TopicRepositoryPort topicRepositoryPort;

    /**
     * 도메인 토픽명으로 db에서 토픽id를 찾아 반환한다.
     * @param topicName 도메인 토픽명
     * @return 토픽id
     */
    @Override
    public Long findTopicIdByName(String topicName) {
        return topicRepositoryPort.findTopicIdByName(topicName);
    }
}
