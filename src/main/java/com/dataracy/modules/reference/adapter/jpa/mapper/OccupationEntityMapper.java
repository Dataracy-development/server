package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.OccupationEntity;
import com.dataracy.modules.reference.domain.model.Occupation;

/**
 * Occupation 엔티티와 Occupation 도메인 모델을 변환하는 매퍼
 */
public final class OccupationEntityMapper {
    private OccupationEntityMapper() {
    }

    // Occupation 엔티티 -> Occupation 도메인 모델
    public static Occupation toDomain(OccupationEntity occupationEntity) {
        if (occupationEntity == null) {
            return null;
        }

        return new Occupation(
                occupationEntity.getId(),
                occupationEntity.getValue(),
                occupationEntity.getLabel()
        );
    }

    // Occupation 도메인 모델 -> Occupation 엔티티
    public static OccupationEntity toEntity(Occupation occupation) {
        if (occupation == null) {
            return null;
        }

        return OccupationEntity.of(
                occupation.value(),
                occupation.label()
        );
    }
}
