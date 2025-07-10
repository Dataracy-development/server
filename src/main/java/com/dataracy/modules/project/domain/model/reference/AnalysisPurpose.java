package com.dataracy.modules.project.domain.model.reference;

/**
 * 분석 목적 도메인 모델
 * @param id 분석 목적 id
 * @param value 분석 목적 값
 * @param label 분석 목적 라벨
 */
public record AnalysisPurpose(
        Long id,
        String value,
        String label
) {}
