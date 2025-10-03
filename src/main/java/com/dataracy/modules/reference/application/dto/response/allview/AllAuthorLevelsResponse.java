/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.dto.response.allview;

import java.util.List;

import com.dataracy.modules.reference.application.dto.response.singleview.AuthorLevelResponse;

/**
 * authorLevels 리스트 조회를 위한 애플리케이션 응답 DTO
 *
 * @param authorLevels authorLevels 리스트
 */
public record AllAuthorLevelsResponse(List<AuthorLevelResponse> authorLevels) {}
