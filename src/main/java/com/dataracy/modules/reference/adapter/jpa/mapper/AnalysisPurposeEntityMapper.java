package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;

/**
 * 분석 목적 엔티티와 분석 목적 도메인 모델을 변환하는 매퍼
 */
public final class AnalysisPurposeEntityMapper {
    private AnalysisPurposeEntityMapper() {
    }

    /**
     * AnalysisPurposeEntity 객체를 AnalysisPurpose 도메인 모델로 변환합니다.
     *
     * @param analysisPurposeEntity 변환할 AnalysisPurposeEntity 객체
     * @return 변환된 AnalysisPurpose 도메인 모델 객체, 입력이 null인 경우 null 반환
     */
    public static AnalysisPurpose toDomain(AnalysisPurposeEntity analysisPurposeEntity) {
        if (analysisPurposeEntity == null) {
            return null;
        }

        return new AnalysisPurpose (
                analysisPurposeEntity.getId(),
                analysisPurposeEntity.getValue(),
                analysisPurposeEntity.getLabel()
        );
    }

    /**
     * AnalysisPurpose 도메인 모델을 AnalysisPurposeEntity JPA 엔티티로 변환합니다.
     *
     * 입력이 null이면 null을 반환합니다. 그렇지 않으면 도메인 모델의 value와 label 값을 사용하여 새로운 엔티티를 생성합니다.
     *
     * @param analysisPurpose 변환할 AnalysisPurpose 도메인 모델
     * @return 변환된 AnalysisPurposeEntity 엔티티 또는 입력이 null인 경우 null
     */
    public static AnalysisPurposeEntity toEntity(AnalysisPurpose analysisPurpose) {
        if (analysisPurpose == null) {
            return null;
        }

        return AnalysisPurposeEntity.of(
                analysisPurpose.value(),
                analysisPurpose.label()
        );
    }
}
