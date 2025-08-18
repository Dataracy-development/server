package com.dataracy.modules.project.application.dto.response.support;

import java.util.Map;

/**
 * 프로젝트 응답을 위한 라벨 매핑 보조 응답 DTO
 *
 * @param usernameMap 유저 닉네임 맵
 * @param topicLabelMap 토픽 라벨 맵
 * @param analysisPurposeLabelMap 분석 목적 라벨 맵
 * @param dataSourceLabelMap 데이터 출처 라벨 맵
 * @param authorLevelLabelMap 작성자 유형 라벨 맵
 */
public record ProjectLabelMapResponse(
        Map<Long, String> usernameMap,
        Map<Long, String> topicLabelMap,
        Map<Long, String> analysisPurposeLabelMap,
        Map<Long, String> dataSourceLabelMap,
        Map<Long, String> authorLevelLabelMap
) {}
