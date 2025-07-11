package com.dataracy.modules.reference.application.mapper;

import com.dataracy.modules.reference.application.dto.response.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AuthorLevel 도메인 DTO와 AuthorLevel 도메인 모델을 변환하는 매퍼
 */
@Component
public class AuthorLevelDtoMapper {
    // AuthorLevel 도메인 모델 -> AuthorLevel 도메인 응답 DTO
    public AllAuthorLevelsResponse.AuthorLevelResponse toResponseDto(AuthorLevel authorLevel) {
        return new AllAuthorLevelsResponse.AuthorLevelResponse(
                authorLevel.id(),
                authorLevel.value(),
                authorLevel.label()
        );
    }

    // 전체 AuthorLevel 리스트 조회 도메인 모델 -> 전체 AuthorLevel 리스트 조회 도메인 응답 DTO
    public AllAuthorLevelsResponse toResponseDto(List<AuthorLevel> authorLevels) {
        return new AllAuthorLevelsResponse(
                authorLevels.stream()
                        .map(this::toResponseDto)
                        .toList()
        );
    }
}
