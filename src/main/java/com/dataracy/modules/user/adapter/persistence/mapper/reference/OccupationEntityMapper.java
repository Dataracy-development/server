package com.dataracy.modules.user.adapter.persistence.mapper.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.OccupationEntity;
import com.dataracy.modules.user.domain.model.reference.Occupation;

/**
 * Occupation 엔티티와 Occupation 도메인 모델을 변환하는 매퍼
 */
public class OccupationEntityMapper {
    // Occupation 엔티티 -> Occupation 도메인 모델
    public static Occupation toDomain(OccupationEntity occupationEntity) {
        return new Occupation(
                occupationEntity.getId(),
                occupationEntity.getValue(),
                occupationEntity.getLabel()
        );
    }

    // Occupation 도메인 모델 -> Occupation 엔티티
    public static OccupationEntity toEntity(Occupation occupation) {
        return OccupationEntity.toEntity(
                occupation.id(),
                occupation.value(),
                occupation.label()
        );
    }
}
