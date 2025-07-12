package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.AuthorLevelWebResponse;

import java.util.List;

/**
 * authorLevel 리스트 조회를 위한 웹응답 DTO
 * @param authorLevels authorLevel 리스트
 */
public record AllAuthorLevelsWebResponse(List<AuthorLevelWebResponse> authorLevels) {
}
