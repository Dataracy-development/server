package com.dataracy.modules.user.adapter.persistence.mapper.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.VisitSourceEntity;
import com.dataracy.modules.user.domain.model.reference.VisitSource;

/**
 * VisitSource 엔티티와 VisitSource 도메인 모델을 변환하는 매퍼
 */
public class VisitSourceEntityMapper {
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
                visitSource.id(),
                visitSource.value(),
                visitSource.label()
        );
    }
}
