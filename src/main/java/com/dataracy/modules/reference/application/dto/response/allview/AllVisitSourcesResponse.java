package com.dataracy.modules.reference.application.dto.response.allview;

import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;

import java.util.List;

/**
 * visitSource 리스트 조회를 위한 도메인 응답 DTO
 * @param visitSources visitSource 리스트
 */
public record AllVisitSourcesResponse(List<VisitSourceResponse> visitSources) {
}
