package com.dataracy.modules.comment.adapter.jpa.mapper;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;
import com.dataracy.modules.comment.domain.model.Comment;

public final class CommentEntityMapper {
    private CommentEntityMapper() {}

    /**
     * CommentEntity 객체를 Comment 도메인 객체로 변환합니다.
     *
     * @param entity 변환할 CommentEntity 객체. null일 경우 null을 반환합니다.
     * @return 변환된 Comment 도메인 객체 또는 입력이 null인 경우 null
     */
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

    /**
     * 도메인 모델 Comment 객체를 CommentEntity로 변환합니다.
     *
     * 입력이 null인 경우 null을 반환합니다. 변환 시 프로젝트 ID, 사용자 ID, 내용, 부모 댓글 ID만 매핑되며, ID, 좋아요 수, 생성일시는 설정되지 않습니다.
     *
     * @param comment 변환할 Comment 도메인 객체
     * @return 변환된 CommentEntity 객체 또는 입력이 null일 경우 null
     */
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
