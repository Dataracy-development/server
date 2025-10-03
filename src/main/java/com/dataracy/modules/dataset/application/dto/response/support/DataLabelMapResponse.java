/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.dto.response.support;

import java.util.Map;

/**
 * 유저명, 토픽, 데이터 출처, 데이터 유형을 추출하여 저장하는 보조 응답 DTO
 *
 * @param usernameMap 사용자 ID를 키로 하는 사용자명 맵
 * @param userProfileUrlMap 사용자 ID를 키로 하는 사용자 프로필 이미지 URL 맵
 * @param topicLabelMap 토픽 ID를 키로 하는 라벨 맵
 * @param dataSourceLabelMap 데이터 소스 ID를 키로 하는 라벨 맵
 * @param dataTypeLabelMap 데이터 타입 ID를 키로 하는 라벨 맵
 */
public record DataLabelMapResponse(
    Map<Long, String> usernameMap,
    Map<Long, String> userProfileUrlMap,
    Map<Long, String> topicLabelMap,
    Map<Long, String> dataSourceLabelMap,
    Map<Long, String> dataTypeLabelMap) {}
