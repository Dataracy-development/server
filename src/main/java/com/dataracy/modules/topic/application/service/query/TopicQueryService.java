package com.dataracy.modules.topic.application.service.query;

import com.dataracy.modules.topic.application.dto.response.AllTopicsResponse;
import com.dataracy.modules.topic.application.mapper.TopicDtoMapper;
import com.dataracy.modules.topic.application.port.in.FindAllTopicsUseCase;
import com.dataracy.modules.topic.application.port.in.FindTopicUseCase;
import com.dataracy.modules.topic.application.port.out.TopicRepositoryPort;
import com.dataracy.modules.topic.domain.model.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicQueryService implements FindTopicUseCase, FindAllTopicsUseCase {

    private final TopicDtoMapper topicDtoMapper;
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

    /**
     * 모든 토픽 리스트를 조회한다.
     * @return 토픽 리스트
     */
    @Override
    public AllTopicsResponse allTopics() {
        List<Topic> topics = topicRepositoryPort.allTopics();
        return topicDtoMapper.toResponseDto(topics);
    }
}
