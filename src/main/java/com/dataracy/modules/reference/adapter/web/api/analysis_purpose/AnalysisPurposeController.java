package com.dataracy.modules.reference.adapter.web.api.analysis_purpose;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.AnalysisPurposeWebMapper;
import com.dataracy.modules.reference.adapter.web.response.AllAnalysisPurposesWebResponse;
import com.dataracy.modules.reference.application.dto.response.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.port.in.analysis_purpose.FindAllAnalysisPurposesUseCase;
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
     * 전체 분석 목적 목록 조회
     */
    @Override
    public ResponseEntity<SuccessResponse<AllAnalysisPurposesWebResponse>> allAnalysisPurposes (
    ) {
        AllAnalysisPurposesResponse allAnalysisPurposesResponse = findAllAnalysisPurposesUseCase.allAnalysisPurposes();
        AllAnalysisPurposesWebResponse allAnalysisPurposesWebResponse = analysisPurposeWebMapper.toWebDto(allAnalysisPurposesResponse);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ReferenceSuccessStatus.OK_TOTAL_ANALYSIS_PURPOSE_LIST, allAnalysisPurposesWebResponse));
    }
}
