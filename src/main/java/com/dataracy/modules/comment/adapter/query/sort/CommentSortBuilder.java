package com.dataracy.modules.comment.adapter.query.sort;

import com.dataracy.modules.comment.adapter.jpa.entity.QCommentEntity;
import com.querydsl.core.types.OrderSpecifier;

public final class CommentSortBuilder {
    /**
 * 이 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private CommentSortBuilder() {}

    private static final QCommentEntity comment = QCommentEntity.commentEntity;

    public static OrderSpecifier<?>[] createdAtDesc() {
        return new OrderSpecifier[]{comment.createdAt.desc()};
    }
}
