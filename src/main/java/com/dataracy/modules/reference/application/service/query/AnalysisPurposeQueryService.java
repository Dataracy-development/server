package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.mapper.AnalysisPurposeDtoMapper;
import com.dataracy.modules.reference.application.port.in.analysis_purpose.FindAllAnalysisPurposesUseCase;
import com.dataracy.modules.reference.application.port.in.analysis_purpose.FindAnalysisPurposeUseCase;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposeRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisPurposeQueryService implements
        FindAllAnalysisPurposesUseCase,
        FindAnalysisPurposeUseCase
{
    private final AnalysisPurposeDtoMapper analysisPurposeDtoMapper;
    private final AnalysisPurposeRepositoryPort analysisPurposeRepositoryPort;

    /**
     * 모든 분석 목적(analysisPurpose) 정보를 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 분석 목적 목록이 포함된 AllAnalysisPurposesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllAnalysisPurposesResponse allAnalysisPurposes() {
        List<AnalysisPurpose> analysisPurposes = analysisPurposeRepositoryPort.allAnalysisPurposes();
        return analysisPurposeDtoMapper.toResponseDto(analysisPurposes);
    }

    /**
     * 주어진 분석 목적 ID로 해당 분석 목적 정보를 조회하여 반환한다.
     *
     * @param analysisPurposeId 조회할 분석 목적의 ID
     * @return 조회된 분석 목적의 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public AllAnalysisPurposesResponse.AnalysisPurposeResponse findAnalysisPurpose(Long analysisPurposeId) {
        AnalysisPurpose analysisPurpose = analysisPurposeRepositoryPort.findAnalysisPurposeById(analysisPurposeId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE));
        return analysisPurposeDtoMapper.toResponseDto(analysisPurpose);
    }
}
