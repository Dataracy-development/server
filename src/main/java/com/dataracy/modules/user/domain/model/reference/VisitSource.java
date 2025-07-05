package com.dataracy.modules.user.domain.model.reference;

/**
 * 방문 경로 도메인 모델
 * @param id 방문 경로 id
 * @param value 방문 경로 값
 * @param label 방문 경로 라벨
 */
public record VisitSource(
        Long id,
        String value,
        String label
) {}
