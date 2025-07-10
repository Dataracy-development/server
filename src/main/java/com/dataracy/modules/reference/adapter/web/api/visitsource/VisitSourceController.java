package com.dataracy.modules.reference.adapter.web.api.visitsource;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.VisitSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.AllVisitSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.port.in.visitsource.FindAllVisitSourcesUseCase;
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
     * 전체 방문 경로 목록 조회
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
