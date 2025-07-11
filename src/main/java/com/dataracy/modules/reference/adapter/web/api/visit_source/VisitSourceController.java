package com.dataracy.modules.reference.adapter.web.api.visit_source;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.VisitSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.AllVisitSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.port.in.visit_source.FindAllVisitSourcesUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VisitSourceController implements VisitSourceApi {
    private final VisitSourceWebMapper visitSourceWebMapper;
    private final FindAllVisitSourcesUseCase findAllVisitSourcesUseCase;
    /**
     * 전체 방문 경로 목록을 조회하여 성공 응답으로 반환합니다.
     *
     * @return 전체 방문 경로 목록이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<AllVisitSourcesWebResponse>> allVisitSources (
    ) {
        AllVisitSourcesResponse allVisitSourcesResponse = findAllVisitSourcesUseCase.allVisitSources();
        AllVisitSourcesWebResponse allVisitSourcesWebResponse = visitSourceWebMapper.toWebDto(allVisitSourcesResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_VISIT_SOURCE_LIST, allVisitSourcesWebResponse));
    }
}
