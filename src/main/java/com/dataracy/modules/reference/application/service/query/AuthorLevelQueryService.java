package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.mapper.AuthorLevelDtoMapper;
import com.dataracy.modules.reference.application.port.in.author_level.FindAllAuthorLevelsUseCase;
import com.dataracy.modules.reference.application.port.in.author_level.FindAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.out.AuthorLevelRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
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
     * 모든 AuthorLevel 엔티티의 목록을 조회하여 응답 DTO로 반환한다.
     *
     * @return 전체 AuthorLevel 정보를 담은 AllAuthorLevelsResponse 객체
     */
    @Override
    @Transactional(readOnly = true)
    public AllAuthorLevelsResponse allAuthorLevels() {
        List<AuthorLevel> authorLevels = authorLevelRepositoryPort.allAuthorLevels();
        return authorLevelDtoMapper.toResponseDto(authorLevels);
    }

    /**
     * 주어진 작성자 유형 ID로 해당 작성자 유형 정보를 조회하여 반환한다.
     *
     * @param authorLevelId 조회할 작성자 유형의 ID
     * @return 조회된 작성자 유형의 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public AllAuthorLevelsResponse.AuthorLevelResponse findAuthorLevel(Long authorLevelId) {
        AuthorLevel authorLevel = authorLevelRepositoryPort.findAuthorLevelById(authorLevelId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL));
        return authorLevelDtoMapper.toResponseDto(authorLevel);
    }
}
