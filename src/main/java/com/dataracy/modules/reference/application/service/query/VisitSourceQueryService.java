package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;
import com.dataracy.modules.reference.application.mapper.VisitSourceDtoMapper;
import com.dataracy.modules.reference.application.port.in.visitsource.FindAllVisitSourcesUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.FindVisitSourceUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.GetVisitSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.reference.application.port.out.VisitSourceRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.VisitSource;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitSourceQueryService implements
        FindAllVisitSourcesUseCase,
        FindVisitSourceUseCase,
        ValidateVisitSourceUseCase,
        GetVisitSourceLabelFromIdUseCase
{
    private final VisitSourceDtoMapper visitSourceDtoMapper;
    private final VisitSourceRepositoryPort visitSourceRepositoryPort;

    /**
     * 모든 방문 소스(VisitSource) 목록을 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 방문 소스 정보를 담은 AllVisitSourcesResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllVisitSourcesResponse findAllVisitSources() {
        List<VisitSource> visitSources = visitSourceRepositoryPort.findAllVisitSources();
        return visitSourceDtoMapper.toResponseDto(visitSources);
    }

    /**
     * 주어진 ID로 방문 경로를 조회하여 VisitSourceResponse DTO로 반환한다.
     *
     * 방문 경로가 존재하지 않으면 ReferenceException을 발생시킨다.
     *
     * @param visitSourceId 조회할 방문 경로의 ID
     * @return 조회된 방문 경로 정보를 담은 VisitSourceResponse DTO
     * @throws ReferenceException 방문 경로가 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public VisitSourceResponse findVisitSource(Long visitSourceId) {
        VisitSource visitSource = visitSourceRepositoryPort.findVisitSourceById(visitSourceId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE));
        return visitSourceDtoMapper.toResponseDto(visitSource);
    }

    /**
     * 방문 출처 ID의 존재 여부를 확인하고, 존재하지 않으면 예외를 발생시킵니다.
     *
     * @param visitSourceId 확인할 방문 출처의 ID
     * @throws ReferenceException 방문 출처가 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateVisitSource(Long visitSourceId) {
        Boolean isExist = visitSourceRepositoryPort.existsVisitSourceById(visitSourceId);
        if (!isExist) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE);
        }
    }

    /**
     * 주어진 방문 출처 ID에 해당하는 라벨을 반환합니다.
     *
     * 방문 출처가 존재하지 않을 경우 {@code ReferenceException}이 발생합니다.
     *
     * @param visitSourceId 조회할 방문 출처의 ID
     * @return 방문 출처의 라벨 문자열
     * @throws ReferenceException 방문 출처를 찾을 수 없는 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long visitSourceId) {
        Optional<String> label = visitSourceRepositoryPort.getLabelById(visitSourceId);
        if (label.isEmpty()) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE);
        }
        return label.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getLabelsByIds(List<Long> visitSourceIds) {
        return visitSourceRepositoryPort.getLabelsByIds(visitSourceIds);
    }
}
