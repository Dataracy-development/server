package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.AuthorLevelEntity;
import com.dataracy.modules.reference.domain.model.AuthorLevel;

/**
 * AuthorLevel 엔티티와 AuthorLevel 도메인 모델을 변환하는 매퍼
 */
public final class AuthorLevelEntityMapper {
    private AuthorLevelEntityMapper() {
    }

    /**
     *
     * @param authorLevelEntity
     * @return
     */
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

    /**
     * AuthorLevel 도메인 모델을 AuthorLevelEntity 엔티티로 변환합니다.
     *
     * @param authorLevel 변환할 AuthorLevel 도메인 모델
     * @return 변환된 AuthorLevelEntity 엔티티, 입력이 null이면 null 반환
     */
    public static AuthorLevelEntity toEntity(AuthorLevel authorLevel) {
        if (authorLevel == null) {
            return null;
        }

        return AuthorLevelEntity.of(
                authorLevel.value(),
                authorLevel.label()
        );
    }
}
