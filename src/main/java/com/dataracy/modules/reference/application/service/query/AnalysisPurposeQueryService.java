package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;
import com.dataracy.modules.reference.application.mapper.AnalysisPurposeDtoMapper;
import com.dataracy.modules.reference.application.port.in.analysispurpose.FindAllAnalysisPurposesUseCase;
import com.dataracy.modules.reference.application.port.in.analysispurpose.FindAnalysisPurposeUseCase;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.analysispurpose.ValidateAnalysisPurposeUseCase;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private final AnalysisPurposePort analysisPurposePort;

    /**
     * 모든 분석 목적(analysisPurpose) 정보를 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 분석 목적 목록이 포함된 AllAnalysisPurposesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllAnalysisPurposesResponse findAllAnalysisPurposes() {
        Instant startTime = LoggerFactory.service().logStart("FindAllAnalysisPurposesUseCase", "모든 분석 목적 정보 조회 서비스 시작");
        List<AnalysisPurpose> analysisPurposes = analysisPurposePort.findAllAnalysisPurposes();
        AllAnalysisPurposesResponse allAnalysisPurposesResponse = analysisPurposeDtoMapper.toResponseDto(analysisPurposes);
        LoggerFactory.service().logSuccess("FindAllAnalysisPurposesUseCase", "모든 분석 목적 정보 조회 서비스 종료", startTime);
        return allAnalysisPurposesResponse;
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
        Instant startTime = LoggerFactory.service().logStart("FindAnalysisPurposeUseCase", "주어진 ID로 분석 목적 조회 서비스 시작 analysisPurposeId=" + analysisPurposeId);
        AnalysisPurpose analysisPurpose = analysisPurposePort.findAnalysisPurposeById(analysisPurposeId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("FindAnalysisPurposeUseCase", "해당 분석 목적이 존재하지 않습니다. analysisPurposeId=" + analysisPurposeId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE);
                });
        AnalysisPurposeResponse analysisPurposeResponse = analysisPurposeDtoMapper.toResponseDto(analysisPurpose);
        LoggerFactory.service().logSuccess("FindAnalysisPurposeUseCase", "주어진 ID로 분석 목적 조회 서비스 종료 analysisPurposeId=" + analysisPurposeId, startTime);
        return analysisPurposeResponse;
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
        Instant startTime = LoggerFactory.service().logStart("ValidateAnalysisPurposeUseCase", "주어진 ID에 해당하는 분석 목적이 존재하는지 확인 서비스 시작 analysisPurposeId=" + analysisPurposeId);
        Boolean isExist = analysisPurposePort.existsAnalysisPurposeById(analysisPurposeId);
        if (!isExist) {
            LoggerFactory.service().logWarning("FindAnalysisPurposeUseCase", "해당 분석 목적이 존재하지 않습니다. analysisPurposeId=" + analysisPurposeId);
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE);
        }
        LoggerFactory.service().logSuccess("ValidateAnalysisPurposeUseCase", "주어진 ID에 해당하는 분석 목적이 존재하는지 확인 서비스 종료 analysisPurposeId=" + analysisPurposeId, startTime);
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
        Instant startTime = LoggerFactory.service().logStart("GetAnalysisPurposeLabelFromIdUseCase", "주어진 분석 목적 ID에 해당하는 라벨을 조회 서비스 시작 analysisPurposeId=" + analysisPurposeId);
        String label = analysisPurposePort.getLabelById(analysisPurposeId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("FindAnalysisPurposeUseCase", "해당 분석 목적이 존재하지 않습니다. analysisPurposeId=" + analysisPurposeId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE);
                });
        LoggerFactory.service().logSuccess("GetAnalysisPurposeLabelFromIdUseCase", "주어진 분석 목적 ID에 해당하는 라벨을 조회 서비스 종료 analysisPurposeId=" + analysisPurposeId, startTime);
        return label;
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
        Instant startTime = LoggerFactory.service().logStart("GetAnalysisPurposeLabelFromIdUseCase", "분석 목적 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작");
        if (analysisPurposeIds == null || analysisPurposeIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> labels = analysisPurposePort.getLabelsByIds(analysisPurposeIds);
        LoggerFactory.service().logSuccess("GetAnalysisPurposeLabelFromIdUseCase", "분석 목적 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료", startTime);
        return labels;
    }
}
