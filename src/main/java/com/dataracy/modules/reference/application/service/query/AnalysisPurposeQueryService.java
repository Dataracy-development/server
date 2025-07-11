package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.mapper.AnalysisPurposeDtoMapper;
import com.dataracy.modules.reference.application.port.in.analysis_purpose.FindAllAnalysisPurposesUseCase;
import com.dataracy.modules.reference.application.port.in.analysis_purpose.FindAnalysisPurposeUseCase;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposeRepositoryPort;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
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
     * 모든 analysisPurpose 리스트를 조회한다.
     * @return analysisPurpose 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public AllAnalysisPurposesResponse allAnalysisPurposes() {
        List<AnalysisPurpose> analysisPurposes = analysisPurposeRepositoryPort.allAnalysisPurposes();
        return analysisPurposeDtoMapper.toResponseDto(analysisPurposes);
    }

    /**
     * 분석 목적 id로 분석 목적을 조회한다.
     * @param analysisPurposeId 분석 목적 id
     * @return 분석 목적
     */
    @Override
    @Transactional(readOnly = true)
    public AllAnalysisPurposesResponse.AnalysisPurposeResponse findAnalysisPurpose(Long analysisPurposeId) {
        AnalysisPurpose analysisPurpose = analysisPurposeRepositoryPort.findAnalysisPurposeById(analysisPurposeId);
        return analysisPurposeDtoMapper.toResponseDto(analysisPurpose);
    }
}
