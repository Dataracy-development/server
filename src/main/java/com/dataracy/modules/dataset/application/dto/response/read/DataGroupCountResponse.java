package com.dataracy.modules.dataset.application.dto.response.read;

/**
 * * 데이터 그룹별 개수 조회 응답
 *
 * @param topicId     토픽 ID
 * @param topicLabel  토픽 라벨
 * @param count       개수
 */
public record DataGroupCountResponse(
        Long topicId,
        String topicLabel,
        Long count
) {}
