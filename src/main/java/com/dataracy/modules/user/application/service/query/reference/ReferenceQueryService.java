package com.dataracy.modules.user.application.service.query.reference;

import com.dataracy.modules.user.application.dto.response.reference.AllAuthorLevelsResponse;
import com.dataracy.modules.user.application.dto.response.reference.AllOccupationsResponse;
import com.dataracy.modules.user.application.dto.response.reference.AllVisitSourcesResponse;
import com.dataracy.modules.user.application.mapper.reference.AuthorLevelDtoMapper;
import com.dataracy.modules.user.application.mapper.reference.OccupationDtoMapper;
import com.dataracy.modules.user.application.mapper.reference.VisitSourceDtoMapper;
import com.dataracy.modules.user.application.port.in.reference.*;
import com.dataracy.modules.user.application.port.out.reference.AuthorLevelRepositoryPort;
import com.dataracy.modules.user.application.port.out.reference.OccupationRepositoryPort;
import com.dataracy.modules.user.application.port.out.reference.VisitSourceRepositoryPort;
import com.dataracy.modules.user.domain.model.reference.AuthorLevel;
import com.dataracy.modules.user.domain.model.reference.Occupation;
import com.dataracy.modules.user.domain.model.reference.VisitSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceQueryService implements
        FindAllAuthorLevelsUseCase,
        FindAllOccupationsUseCase,
        FindAllVisitSourcesUseCase,
        FindAuthorLevelUseCase,
        FindOccupationUseCase,
        FindVisitSourceUseCase
{

    private final AuthorLevelDtoMapper authorLevelDtoMapper;
    private final OccupationDtoMapper occupationDtoMapper;
    private final VisitSourceDtoMapper visitSourceDtoMapper;

    private final AuthorLevelRepositoryPort authorLevelRepositoryPort;
    private final OccupationRepositoryPort occupationRepositoryPort;
    private final VisitSourceRepositoryPort visitSourceRepositoryPort;

    /**
     * 모든 authorLevel 리스트를 조회한다.
     * @return authorLevel 리스트
     */
    @Override
    public AllAuthorLevelsResponse allAuthorLevels() {
        List<AuthorLevel> authorLevels = authorLevelRepositoryPort.allAuthorLevels();
        return authorLevelDtoMapper.toResponseDto(authorLevels);
    }

    /**
     * 모든 occupation 리스트를 조회한다.
     * @return occupation 리스트
     */
    @Override
    public AllOccupationsResponse allOccupations() {
        List<Occupation> occupations = occupationRepositoryPort.allOccupations();
        return occupationDtoMapper.toResponseDto(occupations);
    }

    /**
     * 모든 visitSource 리스트를 조회한다.
     * @return visitSource 리스트
     */
    @Override
    public AllVisitSourcesResponse allVisitSources() {
        List<VisitSource> visitSources = visitSourceRepositoryPort.allVisitSources();
        return visitSourceDtoMapper.toResponseDto(visitSources);
    }

    @Override
    public AuthorLevel findAuthorLevel(Long authorLevelId) {
        return authorLevelRepositoryPort.findAuthorLevelById(authorLevelId);
    }

    @Override
    public Occupation findOccupation(Long occupationId) {
        return occupationRepositoryPort.findOccupationById(occupationId);
    }

    @Override
    public VisitSource findVisitSource(Long visitSourceId) {
        return visitSourceRepositoryPort.findVisitSourceById(visitSourceId);
    }
}
