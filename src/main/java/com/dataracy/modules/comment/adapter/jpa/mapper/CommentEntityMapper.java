package com.dataracy.modules.comment.adapter.jpa.mapper;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.domain.model.Comment;

public final class CommentEntityMapper {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private CommentEntityMapper() {}

    public static Comment toDomain(CommentEntity entity) {
        if (entity == null) return null;

        return Comment.of(
                entity.getId(),
                entity.getProjectId(),
                entity.getUserId(),
                entity.getContent(),
                entity.getParentCommentId(),
                entity.getLikeCount(),
                entity.getCreatedAt()
        );
    }

    public static CommentEntity toEntity(Comment comment) {
        if (comment == null) return null;

        return CommentEntity.of(
                comment.getProjectId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getParentCommentId()
        );
    }
}
