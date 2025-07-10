package com.dataracy.modules.reference.adapter.web.api.topic;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.TopicWebMapper;
import com.dataracy.modules.reference.adapter.web.response.AllTopicsWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllTopicsResponse;
import com.dataracy.modules.reference.application.port.in.topic.FindAllTopicsUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TopicController implements TopicApi {
    private final TopicWebMapper topicWebMapper;
    private final FindAllTopicsUseCase findAllTopicsUseCase;

    /**
     * DB 상의 모든 Topic 목록 조회
     * @return
     */
    @Override
    public ResponseEntity<SuccessResponse<AllTopicsWebResponse>> allTopics (
    ) {
        AllTopicsResponse allTopicsResponse = findAllTopicsUseCase.allTopics();
        AllTopicsWebResponse allTopicsWebResponse = topicWebMapper.toWebDto(allTopicsResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_TOPIC_LIST, allTopicsWebResponse));
    }
}
