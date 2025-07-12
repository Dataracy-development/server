package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.mapper.TopicDtoMapper;
import com.dataracy.modules.reference.application.port.in.topic.FindAllTopicsUseCase;
import com.dataracy.modules.reference.application.port.in.topic.IsExistTopicUseCase;
import com.dataracy.modules.reference.application.port.out.TopicRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.Topic;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public AllTopicsResponse allTopics() {
        List<Topic> topics = topicRepositoryPort.findAllTopics();
        return topicDtoMapper.toResponseDto(topics);
    }

    /**
     * 주어진 토픽 ID에 해당하는 토픽의 존재 여부를 검증합니다.
     *
     * 토픽이 존재하지 않을 경우 ReferenceException을 발생시킵니다.
     *
     * @param topicId 존재 여부를 확인할 토픽의 ID
     */
    @Override
    @Transactional(readOnly = true)
    public void validateTopicById(Long topicId) {
        Boolean isExist = topicRepositoryPort.isExistTopicById(topicId);
        if (!isExist) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME);
        }
    }
}
