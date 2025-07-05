package com.dataracy.modules.topic.application.service.query;

import com.dataracy.modules.topic.application.dto.response.AllTopicsResponse;
import com.dataracy.modules.topic.application.mapper.TopicDtoMapper;
import com.dataracy.modules.topic.application.port.in.FindAllTopicsUseCase;
import com.dataracy.modules.topic.application.port.in.IsExistTopicUseCase;
import com.dataracy.modules.topic.application.port.out.TopicRepositoryPort;
import com.dataracy.modules.topic.domain.exception.TopicException;
import com.dataracy.modules.topic.domain.model.Topic;
import com.dataracy.modules.topic.domain.status.TopicErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicUseCaseQueryService implements FindAllTopicsUseCase, IsExistTopicUseCase {

    private final TopicDtoMapper topicDtoMapper;
    private final TopicRepositoryPort topicRepositoryPort;

    /**
     * 모든 토픽 리스트를 조회한다.
     * @return 토픽 리스트
     */
    @Override
    public AllTopicsResponse allTopics() {
        List<Topic> topics = topicRepositoryPort.allTopics();
        return topicDtoMapper.toResponseDto(topics);
    }

    /**
     * id에 따른 해당 토픽이 존재하는지 여부 확인
     * 해당 토픽이 존재 하지 않으면 에러 발생
     * @param topicId 토픽 id
     */
    @Override
    public void validateTopicById(Long topicId) {
        Boolean isExist = topicRepositoryPort.isExistTopicById(topicId);
        if (!isExist) {
            throw new TopicException(TopicErrorStatus.NOT_FOUND_TOPIC_NAME);
        }
    }
}
