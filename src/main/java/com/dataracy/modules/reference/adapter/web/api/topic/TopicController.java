package com.dataracy.modules.reference.adapter.web.api.topic;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.TopicWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllTopicsWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
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
     * 데이터베이스에 저장된 모든 토픽 목록을 조회하여 반환합니다.
     *
     * @return 모든 토픽 정보를 포함한 성공 응답 객체의 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<AllTopicsWebResponse>> findAllTopics (
    ) {
        AllTopicsResponse allTopicsResponse = findAllTopicsUseCase.findAllTopics();
        AllTopicsWebResponse allTopicsWebResponse = topicWebMapper.toWebDto(allTopicsResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_TOPIC_LIST, allTopicsWebResponse));
    }
}
