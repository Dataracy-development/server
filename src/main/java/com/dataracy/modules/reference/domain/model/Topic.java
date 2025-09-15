package com.dataracy.modules.reference.domain.model;

/**
 * 토픽 도메인 모델
 *
 * @param id 토픽 id
 * @param value 토픽 값
 * @param label 토픽 라벨
 */
public record Topic(
        Long id,
        String value,
        String label
) {}
