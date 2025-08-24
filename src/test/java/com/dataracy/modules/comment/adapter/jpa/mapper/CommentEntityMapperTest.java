package com.dataracy.modules.comment.adapter.jpa.mapper;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.domain.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentEntityMapperTest {

    @Test
    @DisplayName("Entity -> Domain 변환")
    void toDomain() {
        CommentEntity entity = CommentEntity.of(10L, 20L, "내용", null);
        Comment domain = CommentEntityMapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getProjectId()).isEqualTo(10L);
        assertThat(domain.getUserId()).isEqualTo(20L);
        assertThat(domain.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("Domain -> Entity 변환")
    void toEntity() {
        Comment domain = Comment.of(1L, 10L, 20L, "내용", null, 0L, null);
        CommentEntity entity = CommentEntityMapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getProjectId()).isEqualTo(10L);
        assertThat(entity.getUserId()).isEqualTo(20L);
        assertThat(entity.getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("toDomain - null 입력 시 null 반환")
    void toDomainNullInput() {
        assertThat(CommentEntityMapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toEntity - null 입력 시 null 반환")
    void toEntityNullInput() {
        assertThat(CommentEntityMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toEntity - domain 값 누락 시 기본값 처리 확인")
    void toEntityMissingValues() {
        Comment domain = Comment.of(null, 1L, 2L, "내용", null, 0L, null);

        CommentEntity entity = CommentEntityMapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull(); // 매퍼에서 id는 매핑하지 않음
        assertThat(entity.getLikeCount()).isEqualTo(0L); // default
    }
}
