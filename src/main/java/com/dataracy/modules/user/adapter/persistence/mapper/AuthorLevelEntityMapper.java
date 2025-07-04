package com.dataracy.modules.user.adapter.persistence.mapper;

import com.dataracy.modules.user.adapter.persistence.entity.AuthorLevelEntity;
import com.dataracy.modules.user.domain.model.AuthorLevel;

/**
 * AuthorLevel 엔티티와 AuthorLevel 도메인 모델을 변환하는 매퍼
 */
public class AuthorLevelEntityMapper {
    // AuthorLevel 엔티티 -> AuthorLevel 도메인 모델
    public static AuthorLevel toDomain(AuthorLevelEntity authorLevelEntity) {
        return AuthorLevel.toDomain(
                authorLevelEntity.getId(),
                authorLevelEntity.getValue(),
                authorLevelEntity.getLabel()
        );
    }

    // AuthorLevel 도메인 모델 -> AuthorLevel 엔티티
    public static AuthorLevelEntity toEntity(AuthorLevel authorLevel) {
        return AuthorLevelEntity.toEntity(
                authorLevel.getId(),
                authorLevel.getValue(),
                authorLevel.getLabel()
        );
    }
}
