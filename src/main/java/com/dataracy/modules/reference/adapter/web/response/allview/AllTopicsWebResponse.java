package com.dataracy.modules.reference.adapter.web.response.allview;

import com.dataracy.modules.reference.adapter.web.response.singleview.TopicWebResponse;

import java.util.List;

/**
 * 토픽 리스트 조회를 위한 웹응답 DTO
 * @param topics 토픽 리스트
 */
public record AllTopicsWebResponse(List<TopicWebResponse> topics) {
}
