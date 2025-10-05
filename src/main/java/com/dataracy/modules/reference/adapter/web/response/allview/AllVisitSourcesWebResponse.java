package com.dataracy.modules.reference.adapter.web.response.allview;

import java.util.List;

import com.dataracy.modules.reference.adapter.web.response.singleview.VisitSourceWebResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방문 경로 리스트 조회 응답")
public record AllVisitSourcesWebResponse(List<VisitSourceWebResponse> visitSources) {}
