package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.mapper.VisitSourceDtoMapper;
import com.dataracy.modules.reference.application.port.in.visit_source.FindAllVisitSourcesUseCase;
import com.dataracy.modules.reference.application.port.in.visit_source.FindVisitSourceUseCase;
import com.dataracy.modules.reference.application.port.out.VisitSourceRepositoryPort;
import com.dataracy.modules.reference.domain.model.VisitSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitSourceQueryService implements
        FindAllVisitSourcesUseCase,
        FindVisitSourceUseCase
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
    public AllVisitSourcesResponse allVisitSources() {
        List<VisitSource> visitSources = visitSourceRepositoryPort.allVisitSources();
        return visitSourceDtoMapper.toResponseDto(visitSources);
    }

    /**
     * 주어진 방문 경로 ID에 해당하는 방문 경로 정보를 조회하여 응답 DTO로 반환한다.
     *
     * @param visitSourceId 조회할 방문 경로의 ID
     * @return 조회된 방문 경로의 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public AllVisitSourcesResponse.VisitSourceResponse findVisitSource(Long visitSourceId) {
        VisitSource visitSource = visitSourceRepositoryPort.findVisitSourceById(visitSourceId);
        return visitSourceDtoMapper.toResponseDto(visitSource);
    }
}
