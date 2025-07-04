package com.dataracy.modules.user.adapter.persistence.mapper;

import com.dataracy.modules.user.adapter.persistence.entity.OccupationEntity;
import com.dataracy.modules.user.domain.model.Occupation;

/**
 * Occupation 엔티티와 Occupation 도메인 모델을 변환하는 매퍼
 */
public class OccupationEntityMapper {
    // Occupation 엔티티 -> Occupation 도메인 모델
    public static Occupation toDomain(OccupationEntity occupationEntity) {
        return Occupation.toDomain(
                occupationEntity.getId(),
                occupationEntity.getValue(),
                occupationEntity.getLabel()
        );
    }

    // Occupation 도메인 모델 -> Occupation 엔티티
    public static OccupationEntity toEntity(Occupation occupation) {
        return OccupationEntity.toEntity(
                occupation.getId(),
                occupation.getValue(),
                occupation.getLabel()
        );
    }
}
