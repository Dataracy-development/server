package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import com.dataracy.modules.reference.application.mapper.TopicDtoMapper;
import com.dataracy.modules.reference.application.port.in.topic.FindAllTopicsUseCase;
import com.dataracy.modules.reference.application.port.in.topic.FindTopicUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
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
public class TopicQueryService implements
        FindAllTopicsUseCase,
        FindTopicUseCase,
        ValidateTopicUseCase
{
    private final TopicDtoMapper topicDtoMapper;
    private final TopicRepositoryPort topicRepositoryPort;

    /**
     * 모든 토픽을 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 토픽 정보를 담은 AllTopicsResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllTopicsResponse findAllTopics() {
        List<Topic> topics = topicRepositoryPort.findAllTopics();
        return topicDtoMapper.toResponseDto(topics);
    }

    /**
     * 주어진 ID에 해당하는 토픽을 조회하여 응답 DTO로 반환합니다.
     *
     * @param topicId 조회할 토픽의 ID
     * @return 조회된 토픽의 응답 DTO
     * @throws ReferenceException 해당 ID의 토픽이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public TopicResponse findTopic(Long topicId) {
        Topic topic = topicRepositoryPort.findTopicById(topicId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME));
        return topicDtoMapper.toResponseDto(topic);
    }

    /**
     * 주어진 토픽 ID에 해당하는 토픽이 존재하는지 확인합니다.
     *
     * 토픽이 존재하지 않으면 ReferenceException을 발생시킵니다.
     *
     * @param topicId 존재 여부를 확인할 토픽의 ID
     */
    @Override
    @Transactional(readOnly = true)
    public void validateTopic(Long topicId) {
        Boolean isExist = topicRepositoryPort.existsTopicById(topicId);
        if (!isExist) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME);
        }
    }
}
