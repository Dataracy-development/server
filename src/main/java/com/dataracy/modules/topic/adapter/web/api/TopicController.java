package com.dataracy.modules.topic.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.topic.adapter.web.mapper.TopicWebMapper;
import com.dataracy.modules.topic.adapter.web.response.AllTopicsWebResponse;
import com.dataracy.modules.topic.application.dto.response.AllTopicsResponse;
import com.dataracy.modules.topic.application.port.in.FindAllTopicsUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TopicController implements TopicApi {
    private final TopicWebMapper topicWebMapper;
    private final FindAllTopicsUseCase findAllTopicsUseCase;

    @Override
    public ResponseEntity<SuccessResponse<AllTopicsWebResponse>> allTopics (
    ) {
        AllTopicsResponse allTopicsResponse = findAllTopicsUseCase.allTopics();
        AllTopicsWebResponse allTopicsWebResponse = topicWebMapper.toWebDto(allTopicsResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_NOT_DUPLICATED_NICKNAME, allTopicsWebResponse));
    }
}
