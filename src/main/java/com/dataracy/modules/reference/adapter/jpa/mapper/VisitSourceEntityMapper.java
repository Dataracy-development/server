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

    // VisitSource 도메인 모델 -> VisitSource 엔티티
    public static VisitSourceEntity toEntity(VisitSource visitSource) {
        if (visitSource == null) {
            return null;
        }

        return VisitSourceEntity.toEntity(
                visitSource.value(),
                visitSource.label()
        );
    }
}
