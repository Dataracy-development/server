package com.dataracy.modules.project.domain.model.reference;

/**
 * 데이터 출처 도메인 모델
 * @param id 데이터 출처 id
 * @param value 데이터 출처 값
 * @param label 데이터 출처 라벨
 */
public record DataSource(
        Long id,
        String value,
        String label
) {}
