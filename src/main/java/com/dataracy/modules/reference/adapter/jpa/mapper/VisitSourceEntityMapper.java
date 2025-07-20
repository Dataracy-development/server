package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.VisitSourceEntity;
import com.dataracy.modules.reference.domain.model.VisitSource;

/**
 * VisitSource 엔티티와 VisitSource 도메인 모델을 변환하는 매퍼
 */
public final class VisitSourceEntityMapper {
    private VisitSourceEntityMapper() {
    }

    // VisitSource 엔티티 -> VisitSource 도메인 모델
    public static VisitSource toDomain(VisitSourceEntity visitSourceEntity) {
        if (visitSourceEntity == null) {
            return null;
        }

        return new VisitSource(
                visitSourceEntity.getId(),
                visitSourceEntity.getValue(),
                visitSourceEntity.getLabel()
        );
    }

    /**
     * VisitSource 도메인 모델을 VisitSourceEntity JPA 엔티티로 변환합니다.
     *
     * @param visitSource 변환할 VisitSource 도메인 모델
     * @return VisitSourceEntity 객체 또는 입력이 null인 경우 null
     */
    public static VisitSourceEntity toEntity(VisitSource visitSource) {
        if (visitSource == null) {
            return null;
        }

        return VisitSourceEntity.of(
                visitSource.value(),
                visitSource.label()
        );
    }
}
