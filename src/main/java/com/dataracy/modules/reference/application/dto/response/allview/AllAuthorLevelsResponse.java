package com.dataracy.modules.reference.application.dto.response.allview;

import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;

import java.util.List;

/**
 * authorLevels 리스트 조회를 위한 애플리케이션 응답 DTO
 *
 * @param authorLevels authorLevels 리스트
 */
public record AllAuthorLevelsResponse(List<AuthorLevelResponse> authorLevels) {
}
