package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.mapper.AuthorLevelDtoMapper;
import com.dataracy.modules.reference.application.port.in.author_level.FindAllAuthorLevelsUseCase;
import com.dataracy.modules.reference.application.port.in.author_level.FindAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.out.AuthorLevelRepositoryPort;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorLevelQueryService implements
        FindAllAuthorLevelsUseCase,
        FindAuthorLevelUseCase
{
    private final AuthorLevelDtoMapper authorLevelDtoMapper;
    private final AuthorLevelRepositoryPort authorLevelRepositoryPort;

    /**
     * 모든 authorLevel 리스트를 조회한다.
     * @return authorLevel 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public AllAuthorLevelsResponse allAuthorLevels() {
        List<AuthorLevel> authorLevels = authorLevelRepositoryPort.allAuthorLevels();
        return authorLevelDtoMapper.toResponseDto(authorLevels);
    }

    /**
     * 작성자 유형 id로 작성자 유형을 조회한다.
     * @param authorLevelId 작성자 유형 id
     * @return 작성자 유형
     */
    @Override
    @Transactional(readOnly = true)
    public AllAuthorLevelsResponse.AuthorLevelResponse findAuthorLevel(Long authorLevelId) {
        AuthorLevel authorLevel = authorLevelRepositoryPort.findAuthorLevelById(authorLevelId);
        return authorLevelDtoMapper
                .toResponseDto(authorLevel);
    }
}
