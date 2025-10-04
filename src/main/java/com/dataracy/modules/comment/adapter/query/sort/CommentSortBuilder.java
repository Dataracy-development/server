package com.dataracy.modules.comment.adapter.query.sort;

import com.dataracy.modules.comment.adapter.jpa.entity.QCommentEntity;

import com.querydsl.core.types.OrderSpecifier;

public final class CommentSortBuilder {
  /** CommentSortBuilder 클래스의 인스턴스 생성을 방지합니다. */
  private CommentSortBuilder() {}

  private static final QCommentEntity comment = QCommentEntity.commentEntity;

  /**
   * 댓글 엔티티의 생성일(createdAt) 기준 내림차순 정렬 조건을 반환합니다.
   *
   * @return 댓글의 생성일을 기준으로 내림차순 정렬하는 OrderSpecifier 배열
   */
  @SuppressWarnings("rawtypes")
  public static OrderSpecifier[] createdAtDesc() {
    return new OrderSpecifier[] {comment.createdAt.desc()};
  }
}
