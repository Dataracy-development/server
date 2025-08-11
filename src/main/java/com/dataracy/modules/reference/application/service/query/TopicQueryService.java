package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import com.dataracy.modules.reference.application.mapper.TopicDtoMapper;
import com.dataracy.modules.reference.application.port.in.topic.FindAllTopicsUseCase;
import com.dataracy.modules.reference.application.port.in.topic.FindTopicUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import com.dataracy.modules.reference.application.port.out.TopicPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.Topic;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TopicQueryService implements
        FindAllTopicsUseCase,
        FindTopicUseCase,
        ValidateTopicUseCase,
        GetTopicLabelFromIdUseCase
{
    private final TopicDtoMapper topicDtoMapper;
    private final TopicPort topicPort;

    /**
     * 모든 토픽 정보를 조회하여 AllTopicsResponse DTO로 반환한다.
     *
     * @return 전체 토픽 정보를 포함하는 AllTopicsResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllTopicsResponse findAllTopics() {
        Instant startTime = LoggerFactory.service().logStart("FindAllTopicsUseCase", "모든 토픽 정보 조회 서비스 시작");
        List<Topic> topics = topicPort.findAllTopics();
        AllTopicsResponse allTopicsResponse = topicDtoMapper.toResponseDto(topics);
        LoggerFactory.service().logSuccess("FindAllTopicsUseCase", "모든 토픽 정보 조회 서비스 종료", startTime);
        return allTopicsResponse;
    }

    /**
     * 주어진 ID에 해당하는 토픽을 조회하여 응답 DTO로 반환합니다.
     *
     * @param topicId 조회할 토픽의 ID
     * @return 조회된 토픽의 응답 DTO
     * @throws ReferenceException 해당 ID의 토픽이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public TopicResponse findTopic(Long topicId) {
        Instant startTime = LoggerFactory.service().logStart("FindTopicUseCase", "주어진 ID로 토픽 조회 서비스 시작 topicId=" + topicId);
        Topic topic = topicPort.findTopicById(topicId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("FindTopicUseCase", "해당 토픽이 존재하지 않습니다. topicId=" + topicId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME);
                });
        TopicResponse topicResponse = topicDtoMapper.toResponseDto(topic);
        LoggerFactory.service().logSuccess("FindTopicUseCase", "주어진 ID로 토픽 조회 서비스 종료 topicId=" + topicId, startTime);
        return topicResponse;
    }

    /**
     * 주어진 토픽 ID에 해당하는 토픽의 존재 여부를 검증합니다.
     * 토픽이 존재하지 않을 경우 ReferenceException을 발생시킵니다.
     *
     * @param topicId 존재 여부를 확인할 토픽의 ID
     * @throws ReferenceException 해당 ID의 토픽이 존재하지 않을 때 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public void validateTopic(Long topicId) {
        Instant startTime = LoggerFactory.service().logStart("ValidateTopicUseCase", "주어진 ID에 해당하는 토픽이 존재하는지 확인 서비스 시작 topicId=" + topicId);
        Boolean isExist = topicPort.existsTopicById(topicId);
        if (!isExist) {
            LoggerFactory.service().logWarning("ValidateTopicUseCase", "해당 토픽이 존재하지 않습니다. topicId=" + topicId);
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME);
        }
        LoggerFactory.service().logSuccess("ValidateTopicUseCase", "주어진 ID에 해당하는 토픽이 존재하는지 확인 서비스 종료 topicId=" + topicId, startTime);
    }

    /**
     * 주어진 토픽 ID에 해당하는 라벨을 반환합니다.
     *
     * @param topicId 라벨을 조회할 토픽의 ID
     * @return 토픽의 라벨 문자열
     * @throws ReferenceException 토픽이 존재하지 않거나 라벨이 없을 경우 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long topicId) {
        Instant startTime = LoggerFactory.service().logStart("GetTopicLabelFromIdUseCase", "주어진 토픽 ID에 해당하는 라벨을 조회 서비스 시작 topicId=" + topicId);
        String label = topicPort.getLabelById(topicId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("GetTopicLabelFromIdUseCase", "해당 토픽이 존재하지 않습니다. topicId=" + topicId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME);
                });
        LoggerFactory.service().logSuccess("GetTopicLabelFromIdUseCase", "주어진 토픽 ID에 해당하는 라벨을 조회 서비스 종료 topicId=" + topicId, startTime);
        return label;
    }

    /**
     * 주어진 토픽 ID 목록에 대해 각 토픽의 라벨을 조회하여 ID와 라벨의 맵으로 반환합니다.
     *
     * @param topicIds 라벨을 조회할 토픽 ID 목록
     * @return 각 토픽 ID에 해당하는 라벨의 맵. 입력이 null이거나 비어 있으면 빈 맵을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getLabelsByIds(List<Long> topicIds) {
        Instant startTime = LoggerFactory.service().logStart("GetTopicLabelFromIdUseCase", "토픽 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작");
        if (topicIds == null || topicIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> labels = topicPort.getLabelsByIds(topicIds);
        LoggerFactory.service().logSuccess("GetTopicLabelFromIdUseCase", "토픽 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료", startTime);
        return labels;
    }
}
