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
     * 모든 visitSource 리스트를 조회한다.
     * @return visitSource 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public AllVisitSourcesResponse allVisitSources() {
        List<VisitSource> visitSources = visitSourceRepositoryPort.allVisitSources();
        return visitSourceDtoMapper.toResponseDto(visitSources);
    }

    /**
     * 방문 경로 id로 방문 경로를 조회한다.
     * @param visitSourceId 방문 경로 id
     * @return 방문 경로
     */
    @Override
    @Transactional(readOnly = true)
    public AllVisitSourcesResponse.VisitSourceResponse findVisitSource(Long visitSourceId) {
        VisitSource visitSource = visitSourceRepositoryPort.findVisitSourceById(visitSourceId);
        return visitSourceDtoMapper.toResponseDto(visitSource);
    }
}
