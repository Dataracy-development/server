package com.dataracy.modules.reference.adapter.web.api.analysispurpose;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.AnalysisPurposeWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllAnalysisPurposesWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.port.in.analysispurpose.FindAllAnalysisPurposesUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AnalysisPurposeController implements AnalysisPurposeApi {
    private final AnalysisPurposeWebMapper analysisPurposeWebMapper;
    private final FindAllAnalysisPurposesUseCase findAllAnalysisPurposesUseCase;
    /**
     * 전체 분석 목적 목록을 조회하여 성공 응답으로 반환합니다.
     *
     * @return 전체 분석 목적 목록이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<AllAnalysisPurposesWebResponse>> findAllAnalysisPurposes (
    ) {
        AllAnalysisPurposesResponse allAnalysisPurposesResponse = findAllAnalysisPurposesUseCase.findAllAnalysisPurposes();
        AllAnalysisPurposesWebResponse allAnalysisPurposesWebResponse = analysisPurposeWebMapper.toWebDto(allAnalysisPurposesResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_ANALYSIS_PURPOSE_LIST, allAnalysisPurposesWebResponse));
    }
}
