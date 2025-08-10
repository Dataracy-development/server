package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.OccupationEntity;
import com.dataracy.modules.reference.domain.model.Occupation;

/**
 * Occupation 엔티티와 Occupation 도메인 모델을 변환하는 매퍼
 */
public final class OccupationEntityMapper {
    private OccupationEntityMapper() {
    }

    /**
     * OccupationEntity 객체를 Occupation 도메인 모델로 변환합니다.
     *
     * @param occupationEntity 변환할 OccupationEntity 객체
     * @return 변환된 Occupation 도메인 모델 객체, 입력이 null이면 null 반환
     */
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

    /**
     * Occupation 도메인 모델을 OccupationEntity JPA 엔티티로 변환합니다.
     *
     * @param occupation 변환할 Occupation 도메인 모델
     * @return 변환된 OccupationEntity 엔티티, 입력이 null이면 null 반환
     */
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
