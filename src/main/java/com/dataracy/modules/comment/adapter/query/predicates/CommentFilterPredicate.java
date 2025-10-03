/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.adapter.query.predicates;

import static com.dataracy.modules.comment.adapter.jpa.entity.QCommentEntity.commentEntity;

import com.querydsl.core.types.dsl.BooleanExpression;

public class CommentFilterPredicate {
  /**
   * `CommentFilterPredicate` 클래스의 인스턴스 생성을 방지하는 private 생성자입니다.
   *
   * <p>이 클래스는 정적 메서드만을 제공하는 유틸리티 클래스이므로 외부에서 인스턴스화할 수 없습니다.
   */
  private CommentFilterPredicate() {}

  /**
   * 주어진 댓글 ID와 일치하는 댓글을 필터링하는 QueryDSL BooleanExpression을 반환합니다.
   *
   * @param commentId 필터링할 댓글의 ID
   * @return 댓글 ID가 일치하는 경우 해당 조건의 BooleanExpression, commentId가 null이면 null 반환
   */
  public static BooleanExpression commentIdEq(Long commentId) {
    return commentId == null ? null : commentEntity.id.eq(commentId);
  }

  /**
   * 주어진 프로젝트 ID와 일치하는 댓글을 필터링하는 QueryDSL 조건식을 반환합니다.
   *
   * @param projectId 필터링할 프로젝트의 ID. null이면 조건식을 반환하지 않습니다.
   * @return 프로젝트 ID가 일치하는 댓글에 대한 BooleanExpression, 또는 null
   */
  public static BooleanExpression projectIdEq(Long projectId) {
    return projectId == null ? null : commentEntity.projectId.eq(projectId);
  }

  /**
   * 주어진 부모 댓글 ID와 일치하는 댓글을 필터링하는 QueryDSL 조건식을 반환합니다.
   *
   * @param commentId 부모 댓글의 ID
   * @return 부모 댓글 ID가 일치하는 조건식, 입력이 null이면 null 반환
   */
  public static BooleanExpression parentCommentIdEq(Long commentId) {
    return commentId == null ? null : commentEntity.parentCommentId.eq(commentId);
  }

  /**
   * 부모 댓글이 없는 루트 댓글을 필터링하는 조건식을 반환합니다.
   *
   * @return 부모 댓글 ID가 null인 댓글을 찾는 BooleanExpression
   */
  public static BooleanExpression isRootComment() {
    return commentEntity.parentCommentId.isNull();
  }
}
