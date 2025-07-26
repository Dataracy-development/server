package com.dataracy.modules.comment.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;

import static com.dataracy.modules.comment.adapter.jpa.entity.QCommentEntity.commentEntity;

public class CommentFilterPredicate {
    /**
 * `ProjectDataFilterPredicate` 클래스의 인스턴스 생성을 방지합니다.
 *
 * 이 클래스는 정적 메서드만을 제공하는 유틸리티 클래스이므로, 외부에서 인스턴스화하지 못하도록 private 생성자를 정의합니다.
 */
private CommentFilterPredicate() {}

    public static BooleanExpression commentIdEq(Long commentId) {
        return commentId == null ? null : commentEntity.id.eq(commentId);
    }

    public static BooleanExpression projectIdEq(Long projectId) {
        return projectId == null ? null : commentEntity.projectId.eq(projectId);
    }

    public static BooleanExpression isRootComment() {
        return commentEntity.parentCommentId.isNull();
    }
}
