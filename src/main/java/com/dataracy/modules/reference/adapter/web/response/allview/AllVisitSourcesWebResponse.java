package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.VisitSourceWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * visitSource 리스트 조회를 위한 웹응답 DTO
 * @param visitSources visitSource 리스트
 */
@Schema(description = "방문 경로 리스트 조회 응답")
public record AllVisitSourcesWebResponse(List<VisitSourceWebResponse> visitSources) {
}
