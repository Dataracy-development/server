package com.dataracy.modules.reference.adapter.persistence.mapper;

import com.dataracy.modules.reference.adapter.persistence.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;

/**
 * 분석 목적 엔티티와 분석 목적 도메인 모델을 변환하는 매퍼
 */
public class AnalysisPurposeEntityMapper {
    // AnalysisPurpose 엔티티 -> AnalysisPurpose 도메인 모델
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

    // AnalysisPurpose 도메인 모델 -> AnalysisPurpose 엔티티
    public static AnalysisPurposeEntity toEntity(AnalysisPurpose analysisPurpose) {
        if (analysisPurpose == null) {
            return null;
        }

        return AnalysisPurposeEntity.toEntity(
                analysisPurpose.id(),
                analysisPurpose.value(),
                analysisPurpose.label()
        );
    }
}
