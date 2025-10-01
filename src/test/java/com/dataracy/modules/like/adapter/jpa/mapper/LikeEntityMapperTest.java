package com.dataracy.modules.like.adapter.jpa.mapper;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.model.Like;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LikeEntityMapperTest {

    @Test
    @DisplayName("도메인이 null이면 null 반환")
    void toEntityNull() {
        // when
        LikeEntity result = LikeEntityMapper.toEntity(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("도메인을 엔티티로 매핑")
    void toEntityMapping() {
        // given
        Like like = Like.of(null, 7L, TargetType.COMMENT, 123L);

        // when
        LikeEntity entity = LikeEntityMapper.toEntity(like);

        // then
        assertAll(
                () -> assertThat(entity.getTargetId()).isEqualTo(7L),
                () -> assertThat(entity.getTargetType()).isEqualTo(TargetType.COMMENT),
                () -> assertThat(entity.getUserId()).isEqualTo(123L)
        );
    }

    @Test
    @DisplayName("엔티티가 null이면 null 반환")
    void toDomainNull() {
        // when
        Like result = LikeEntityMapper.toDomain(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("엔티티를 도메인으로 매핑")
    void toDomainMapping() {
        // given
        LikeEntity entity = LikeEntity.of(11L, TargetType.PROJECT, 42L);

        // when
        Like like = LikeEntityMapper.toDomain(entity);

        // then
        assertAll(
                () -> assertThat(like.getTargetId()).isEqualTo(11L),
                () -> assertThat(like.getTargetType()).isEqualTo(TargetType.PROJECT),
                () -> assertThat(like.getUserId()).isEqualTo(42L)
        );
    }
}
