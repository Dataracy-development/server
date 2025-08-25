package com.dataracy.modules.like.adapter.jpa.mapper;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.model.Like;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LikeEntityMapperTest {

    @Test
    @DisplayName("toEntity_should_return_null_when_domain_is_null")
    void toEntity_should_return_null_when_domain_is_null() {
        // when
        LikeEntity result = LikeEntityMapper.toEntity(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toEntity_should_map_fields_correctly")
    void toEntity_should_map_fields_correctly() {
        // given
        Like like = Like.of(null, 7L, TargetType.COMMENT, 123L);

        // when
        LikeEntity entity = LikeEntityMapper.toEntity(like);

        // then
        assertThat(entity.getTargetId()).isEqualTo(7L);
        assertThat(entity.getTargetType()).isEqualTo(TargetType.COMMENT);
        assertThat(entity.getUserId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("toDomain_should_return_null_when_entity_is_null")
    void toDomain_should_return_null_when_entity_is_null() {
        // when
        Like result = LikeEntityMapper.toDomain(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toDomain_should_map_fields_correctly")
    void toDomain_should_map_fields_correctly() {
        // given
        LikeEntity entity = LikeEntity.of(11L, TargetType.PROJECT, 42L);

        // when
        Like like = LikeEntityMapper.toDomain(entity);

        // then
        assertThat(like.getTargetId()).isEqualTo(11L);
        assertThat(like.getTargetType()).isEqualTo(TargetType.PROJECT);
        assertThat(like.getUserId()).isEqualTo(42L);
    }
}
