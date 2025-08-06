package com.dataracy.modules.reference.adapter.web.api.visitsource;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.web.mapper.VisitSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllVisitSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.port.in.visitsource.FindAllVisitSourcesUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class VisitSourceController implements VisitSourceApi {
    private final VisitSourceWebMapper visitSourceWebMapper;
    private final FindAllVisitSourcesUseCase findAllVisitSourcesUseCase;
    /**
     * 전체 방문 경로 목록을 조회하여 HTTP 200 성공 응답으로 반환합니다.
     *
     * @return 전체 방문 경로 목록이 포함된 성공 응답 ResponseEntity 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<AllVisitSourcesWebResponse>> findAllVisitSources (
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[FindAllVisitSources] 전체 방문 경로 목록을 조회 API 요청 시작");
        AllVisitSourcesWebResponse allVisitSourcesWebResponse;

        try {
            AllVisitSourcesResponse allVisitSourcesResponse = findAllVisitSourcesUseCase.findAllVisitSources();
            allVisitSourcesWebResponse = visitSourceWebMapper.toWebDto(allVisitSourcesResponse);
        } finally {
            LoggerFactory.api().logResponse("[FindAllVisitSources] 전체 방문 경로 목록을 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_VISIT_SOURCE_LIST, allVisitSourcesWebResponse));
    }
}
