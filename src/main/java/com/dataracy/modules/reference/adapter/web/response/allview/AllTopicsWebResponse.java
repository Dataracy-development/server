package com.dataracy.modules.reference.adapter.web.response.allview;

import java.util.List;

import com.dataracy.modules.reference.adapter.web.response.singleview.TopicWebResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "도메인 리스트 조회 응답")
public record AllTopicsWebResponse(List<TopicWebResponse> topics) {}
