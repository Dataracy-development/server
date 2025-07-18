package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;
import com.dataracy.modules.reference.application.mapper.AuthorLevelDtoMapper;
import com.dataracy.modules.reference.application.port.in.authorlevel.FindAllAuthorLevelsUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.FindAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.out.AuthorLevelRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorLevelQueryService implements
        FindAllAuthorLevelsUseCase,
        FindAuthorLevelUseCase,
        ValidateAuthorLevelUseCase,
        GetAuthorLevelLabelFromIdUseCase
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
    public AllAuthorLevelsResponse findAllAuthorLevels() {
        List<AuthorLevel> authorLevels = authorLevelRepositoryPort.findAllAuthorLevels();
        return authorLevelDtoMapper.toResponseDto(authorLevels);
    }

    /**
     * 주어진 ID로 작성자 등급 정보를 조회하여 반환한다.
     *
     * @param authorLevelId 조회할 작성자 등급의 ID
     * @return 해당 ID의 작성자 등급 정보를 담은 AuthorLevelResponse 객체
     * @throws ReferenceException 작성자 등급이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public AuthorLevelResponse findAuthorLevel(Long authorLevelId) {
        AuthorLevel authorLevel = authorLevelRepositoryPort.findAuthorLevelById(authorLevelId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL));
        return authorLevelDtoMapper.toResponseDto(authorLevel);
    }

    /**
     * 주어진 ID의 저자 등급이 존재하는지 검증합니다.
     *
     * 존재하지 않을 경우 {@code ReferenceException}을 발생시킵니다.
     *
     * @param authorLevelId 검증할 저자 등급의 ID
     * @throws ReferenceException 저자 등급이 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateAuthorLevel(Long authorLevelId) {
        Boolean isExist = authorLevelRepositoryPort.existsAuthorLevelById(authorLevelId);
        if (!isExist) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getLabelById(Long authorLevelId) {
        Optional<String> label = authorLevelRepositoryPort.getLabelById(authorLevelId);
        if (label.isEmpty()) {
            throw new ReferenceException(ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL);
        }
        return label.get();
    }
}
