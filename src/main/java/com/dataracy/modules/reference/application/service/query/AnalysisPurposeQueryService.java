package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;
import com.dataracy.modules.reference.application.mapper.AnalysisPurposeDtoMapper;
import com.dataracy.modules.reference.application.port.in.analysispurpose.FindAllAnalysisPurposesUseCase;
import com.dataracy.modules.reference.application.port.in.analysispurpose.FindAnalysisPurposeUseCase;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.analysispurpose.ValidateAnalysisPurposeUseCase;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposeRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisPurposeQueryService implements
        FindAllAnalysisPurposesUseCase,
        FindAnalysisPurposeUseCase,
        ValidateAnalysisPurposeUseCase,
        GetAnalysisPurposeLabelFromIdUseCase
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
    public AllAnalysisPurposesResponse findAllAnalysisPurposes() {
        List<AnalysisPurpose> analysisPurposes = analysisPurposeRepositoryPort.findAllAnalysisPurposes();
        return analysisPurposeDtoMapper.toResponseDto(analysisPurposes);
    }

    /**
     * 주어진 ID로 분석 목적을 조회하여 상세 응답 DTO로 반환한다.
     *
     * 분석 목적이 존재하지 않으면 ReferenceException을 발생시킨다.
     *
     * @param analysisPurposeId 조회할 분석 목적의 ID
     * @return 조회된 분석 목적의 상세 정보를 담은 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public AnalysisPurposeResponse findAnalysisPurpose(Long analysisPurposeId) {
        AnalysisPurpose analysisPurpose = analysisPurposeRepositoryPort.findAnalysisPurposeById(analysisPurposeId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE));
        return analysisPurposeDtoMapper.toResponseDto(analysisPurpose);
    }

    /**
     * 주어진 ID에 해당하는 분석 목적이 존재하는지 확인합니다.
     *
     * 분석 목적이 존재하지 않을 경우 {@link ReferenceException}을 발생시킵니다.
     *
     * @param analysisPurposeId 존재 여부를 확인할 분석 목적의 ID
     * @throws ReferenceException 분석 목적이 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateAnalysisPurpose(Long analysisPurposeId) {
        Boolean isExist = analysisPurposeRepositoryPort.existsAnalysisPurposeById(analysisPurposeId);
        if (!isExist) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE);
        }
    }

    /**
     * 주어진 분석 목적 ID에 해당하는 라벨을 조회합니다.
     *
     * @param analysisPurposeId 조회할 분석 목적의 ID
     * @return 분석 목적의 라벨 문자열
     * @throws ReferenceException 해당 ID의 분석 목적이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long analysisPurposeId) {
        return analysisPurposeRepositoryPort.getLabelById(analysisPurposeId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE));
    }

    /**
     * 주어진 분석 목적 ID 목록에 대해 각 ID에 해당하는 라벨을 반환합니다.
     *
     * @param analysisPurposeIds 라벨을 조회할 분석 목적 ID 목록
     * @return 각 ID와 해당 라벨이 매핑된 Map. 입력이 null이거나 비어 있으면 빈 Map을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getLabelsByIds(List<Long> analysisPurposeIds) {
        if (analysisPurposeIds == null || analysisPurposeIds.isEmpty()) {
            return Map.of();
        }
        return analysisPurposeRepositoryPort.getLabelsByIds(analysisPurposeIds);
    }
}
