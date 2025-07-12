package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.VisitSourceWebResponse;

import java.util.List;

/**
 * visitSource 리스트 조회를 위한 웹응답 DTO
 * @param visitSources visitSource 리스트
 */
public record AllVisitSourcesWebResponse(List<VisitSourceWebResponse> visitSources) {
}
