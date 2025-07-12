package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.TopicWebResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 토픽 리스트 조회를 위한 웹응답 DTO
 * @param topics 토픽 리스트
 */
@Schema(description = "도메인 리스트 조회 응답")
public record AllTopicsWebResponse(List<TopicWebResponse> topics) {
}
