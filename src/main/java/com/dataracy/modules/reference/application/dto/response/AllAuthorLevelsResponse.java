package com.dataracy.modules.reference.application.dto.response;

import java.util.List;

/**
 * authorLevels 리스트 조회를 위한 도메인 응답 DTO
 * @param authorLevels authorLevels 리스트
 */
public record AllAuthorLevelsResponse(List<AuthorLevelResponse> authorLevels) {
    public record AuthorLevelResponse(Long id, String value, String label
    ) {}
}
