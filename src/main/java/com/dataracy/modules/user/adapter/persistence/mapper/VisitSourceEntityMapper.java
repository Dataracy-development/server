package com.dataracy.modules.user.adapter.persistence.mapper;

import com.dataracy.modules.user.adapter.persistence.entity.VisitSourceEntity;
import com.dataracy.modules.user.domain.model.VisitSource;

/**
 * VisitSource 엔티티와 VisitSource 도메인 모델을 변환하는 매퍼
 */
public class VisitSourceEntityMapper {
    // VisitSource 엔티티 -> VisitSource 도메인 모델
    public static VisitSource toDomain(VisitSourceEntity visitSourceEntity) {
        return VisitSource.toDomain(
                visitSourceEntity.getId(),
                visitSourceEntity.getValue(),
                visitSourceEntity.getLabel()
        );
    }

    // VisitSource 도메인 모델 -> VisitSource 엔티티
    public static VisitSourceEntity toEntity(VisitSource visitSource) {
        return VisitSourceEntity.toEntity(
                visitSource.getId(),
                visitSource.getValue(),
                visitSource.getLabel()
        );
    }
}
