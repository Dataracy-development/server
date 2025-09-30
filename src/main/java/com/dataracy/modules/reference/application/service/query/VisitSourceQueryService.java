package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;
import com.dataracy.modules.reference.application.mapper.VisitSourceDtoMapper;
import com.dataracy.modules.reference.application.port.in.visitsource.FindAllVisitSourcesUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.FindVisitSourceUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.GetVisitSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.reference.application.port.out.VisitSourcePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.VisitSource;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VisitSourceQueryService implements
        FindAllVisitSourcesUseCase,
        FindVisitSourceUseCase,
        ValidateVisitSourceUseCase,
        GetVisitSourceLabelFromIdUseCase
{
    private final VisitSourceDtoMapper visitSourceDtoMapper;
    private final VisitSourcePort visitSourcePort;

    // Use Case 상수 정의
    private static final String FIND_ALL_VISIT_SOURCES_USE_CASE = "FindAllVisitSourcesUseCase";
    private static final String FIND_VISIT_SOURCE_USE_CASE = "FindVisitSourceUseCase";
    private static final String VALIDATE_VISIT_SOURCE_USE_CASE = "ValidateVisitSourceUseCase";
    private static final String GET_VISIT_SOURCE_LABEL_FROM_ID_USE_CASE = "GetVisitSourceLabelFromIdUseCase";
    
    // 메시지 상수 정의
    private static final String VISIT_SOURCE_NOT_FOUND_MESSAGE = "해당 방문 경로가 존재하지 않습니다. visitSourceId=";

    /**
     * 모든 방문 소스(VisitSource)의 전체 목록을 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 방문 소스 정보를 포함하는 AllVisitSourcesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllVisitSourcesResponse findAllVisitSources() {
        Instant startTime = LoggerFactory.service().logStart(FIND_ALL_VISIT_SOURCES_USE_CASE, "모든 방문 경로 정보 조회 서비스 시작");
        List<VisitSource> visitSources = visitSourcePort.findAllVisitSources();
        AllVisitSourcesResponse allVisitSourcesResponse = visitSourceDtoMapper.toResponseDto(visitSources);
        LoggerFactory.service().logSuccess(FIND_ALL_VISIT_SOURCES_USE_CASE, "모든 방문 경로 정보 조회 서비스 종료", startTime);
        return allVisitSourcesResponse;
    }

    /**
     * 주어진 ID에 해당하는 방문 경로 정보를 조회하여 VisitSourceResponse DTO로 반환한다.
     * 방문 경로가 존재하지 않을 경우 ReferenceException이 발생한다.
     *
     * @param visitSourceId 조회할 방문 경로의 ID
     * @return 조회된 방문 경로 정보를 담은 VisitSourceResponse DTO
     * @throws ReferenceException 방문 경로가 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public VisitSourceResponse findVisitSource(Long visitSourceId) {
        Instant startTime = LoggerFactory.service().logStart(FIND_VISIT_SOURCE_USE_CASE, "주어진 ID로 방문 경로 조회 서비스 시작 visitSourceId=" + visitSourceId);
        VisitSource visitSource = visitSourcePort.findVisitSourceById(visitSourceId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(FIND_VISIT_SOURCE_USE_CASE, VISIT_SOURCE_NOT_FOUND_MESSAGE + visitSourceId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE);
                });
        VisitSourceResponse visitSourceResponse = visitSourceDtoMapper.toResponseDto(visitSource);
        LoggerFactory.service().logSuccess(FIND_VISIT_SOURCE_USE_CASE, "주어진 ID로 방문 경로 조회 서비스 종료 visitSourceId=" + visitSourceId, startTime);
        return visitSourceResponse;
    }

    /**
     * 주어진 방문 출처 ID가 존재하는지 검증합니다.
     * 방문 출처가 존재하지 않을 경우 ReferenceException을 발생시킵니다.
     *
     * @param visitSourceId 존재 여부를 확인할 방문 출처의 ID
     * @throws ReferenceException 방문 출처가 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateVisitSource(Long visitSourceId) {
        Instant startTime = LoggerFactory.service().logStart(VALIDATE_VISIT_SOURCE_USE_CASE, "주어진 ID에 해당하는 방문 경로가 존재하는지 확인 서비스 시작 visitSourceId=" + visitSourceId);
        Boolean isExist = visitSourcePort.existsVisitSourceById(visitSourceId);
        if (!isExist) {
            LoggerFactory.service().logWarning(VALIDATE_VISIT_SOURCE_USE_CASE, VISIT_SOURCE_NOT_FOUND_MESSAGE + visitSourceId);
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE);
        }
        LoggerFactory.service().logSuccess(VALIDATE_VISIT_SOURCE_USE_CASE, "주어진 ID에 해당하는 방문 경로가 존재하는지 확인 서비스 종료 visitSourceId=" + visitSourceId, startTime);
    }

    /**
     * 주어진 방문 출처 ID에 해당하는 라벨을 반환합니다.
     * 방문 출처가 존재하지 않을 경우 ReferenceException이 발생합니다.
     *
     * @param visitSourceId 라벨을 조회할 방문 출처의 ID
     * @return 방문 출처의 라벨 문자열
     * @throws ReferenceException 방문 출처를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long visitSourceId) {
        Instant startTime = LoggerFactory.service().logStart(GET_VISIT_SOURCE_LABEL_FROM_ID_USE_CASE, "주어진 방문 경로 ID에 해당하는 라벨을 조회 서비스 시작 visitSourceId=" + visitSourceId);
        String label = visitSourcePort.getLabelById(visitSourceId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(GET_VISIT_SOURCE_LABEL_FROM_ID_USE_CASE, VISIT_SOURCE_NOT_FOUND_MESSAGE + visitSourceId);
                    return new ReferenceException(ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE);
                });
        LoggerFactory.service().logSuccess(GET_VISIT_SOURCE_LABEL_FROM_ID_USE_CASE, "주어진 방문 경로 ID에 해당하는 라벨을 조회 서비스 종료 visitSourceId=" + visitSourceId, startTime);
        return label;
    }

    /**
     * 주어진 방문 출처 ID 목록에 대해 각 ID와 해당 라벨을 매핑한 Map을 반환합니다.
     * 입력된 ID 목록이 null이거나 비어 있으면 빈 Map을 반환합니다.
     *
     * @param visitSourceIds 방문 출처 ID 목록
     * @return 각 ID를 키로 하고 라벨을 값으로 하는 Map
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getLabelsByIds(List<Long> visitSourceIds) {
        Instant startTime = LoggerFactory.service().logStart(GET_VISIT_SOURCE_LABEL_FROM_ID_USE_CASE, "방문 경로 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작");
        if (visitSourceIds == null || visitSourceIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> labels = visitSourcePort.getLabelsByIds(visitSourceIds);
        LoggerFactory.service().logSuccess(GET_VISIT_SOURCE_LABEL_FROM_ID_USE_CASE, "방문 경로 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료", startTime);
        return labels;
    }
}
