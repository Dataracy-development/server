package com.dataracy.modules.reference.adapter.web.response;

import java.util.List;

/**
 * authorLevel 리스트 조회를 위한 웹응답 DTO
 * @param authorLevels authorLevel 리스트
 */
public record AllAuthorLevelsWebResponse(List<AuthorLevelWebResponse> authorLevels) {
    public record AuthorLevelWebResponse(
            Long id,
            String value,
            String label
    ) {}
}
