package com.dataracy.modules.reference.application.dto.response.allview;

import java.util.List;

import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;

/**
 * visitSource 리스트 조회를 위한 애플리케이션 응답 DTO
 *
 * @param visitSources visitSource 리스트
 */
public record AllVisitSourcesResponse(List<VisitSourceResponse> visitSources) {}
