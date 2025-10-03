/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.adapter.jpa.impl.command;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import com.dataracy.modules.comment.application.port.out.command.update.UpdateCommentLikePort;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UpdateCommentLikeDbAdapter implements UpdateCommentLikePort {
  private final CommentJpaRepository commentJpaRepository;

  // Entity 상수 정의
  private static final String COMMENT_ENTITY = "CommentEntity";

  /**
   * 주어진 댓글 ID에 해당하는 댓글의 좋아요 수를 1 증가시킵니다.
   *
   * @param commentId 좋아요 수를 증가시킬 댓글의 ID
   */
  @Override
  public void increaseLikeCount(Long commentId) {
    commentJpaRepository.increaseLikeCount(commentId);
    LoggerFactory.db().logUpdate(COMMENT_ENTITY, String.valueOf(commentId), "댓글 좋아요가 완료되었습니다.");
  }

  /**
   * 지정된 댓글의 좋아요 수를 1 감소시킵니다.
   *
   * @param commentId 좋아요 수를 감소시킬 댓글의 ID
   */
  @Override
  public void decreaseLikeCount(Long commentId) {
    commentJpaRepository.decreaseLikeCount(commentId);
    LoggerFactory.db().logUpdate(COMMENT_ENTITY, String.valueOf(commentId), "댓글 좋아요 취소가 완료되었습니다.");
  }
}
