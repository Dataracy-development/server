package com.dataracy.modules.user.adapter.persistence.mapper.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.AuthorLevelEntity;
import com.dataracy.modules.user.domain.model.reference.AuthorLevel;

/**
 * AuthorLevel 엔티티와 AuthorLevel 도메인 모델을 변환하는 매퍼
 */
public class AuthorLevelEntityMapper {
    // AuthorLevel 엔티티 -> AuthorLevel 도메인 모델
    public static AuthorLevel toDomain(AuthorLevelEntity authorLevelEntity) {
        if (authorLevelEntity == null) {
            return null;
        }

        return new AuthorLevel(
                authorLevelEntity.getId(),
                authorLevelEntity.getValue(),
                authorLevelEntity.getLabel()
        );
    }

    // AuthorLevel 도메인 모델 -> AuthorLevel 엔티티
    public static AuthorLevelEntity toEntity(AuthorLevel authorLevel) {
        if (authorLevel == null) {
            return null;
        }

        return AuthorLevelEntity.toEntity(
                authorLevel.id(),
                authorLevel.value(),
                authorLevel.label()
        );
    }
}
