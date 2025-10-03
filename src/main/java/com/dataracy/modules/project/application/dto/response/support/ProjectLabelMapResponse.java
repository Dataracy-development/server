/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.dto.response.support;

import java.util.Map;

/**
 * 프로젝트 상세 응답에서 사용하는 라벨/사용자명 매핑 DTO. 각 Map의 키는 해당 엔터티의 ID, 값은 클라이언트에 표시할 문자열입니다.
 *
 * @param usernameMap 사용자 ID → 닉네임
 * @param userProfileUrlMap 사용자 ID를 키로 하는 사용자 프로필 이미지 URL 맵
 * @param topicLabelMap 토픽 라벨 ID → 라벨명
 * @param analysisPurposeLabelMap 분석 목적 라벨 ID → 라벨명
 * @param dataSourceLabelMap 데이터 출처 라벨 ID → 라벨명
 * @param authorLevelLabelMap 작성자 레벨 라벨 ID → 라벨명
 */
public record ProjectLabelMapResponse(
    Map<Long, String> usernameMap,
    Map<Long, String> userProfileUrlMap,
    Map<Long, String> topicLabelMap,
    Map<Long, String> analysisPurposeLabelMap,
    Map<Long, String> dataSourceLabelMap,
    Map<Long, String> authorLevelLabelMap) {}
