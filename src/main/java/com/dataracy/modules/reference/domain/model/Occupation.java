package com.dataracy.modules.reference.domain.model;

/**
 * 경험 도메인 모델
 *
 * @param id 경험 id
 * @param value 경험 값
 * @param label 경험 라벨
 */
public record Occupation(
        Long id,
        String value,
        String label
) {}
