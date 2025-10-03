/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.application.port.in.query.validate;

public interface ValidateCommentUseCase {
  /**
   * 주어진 ID에 해당하는 댓글이 존재하는지 유효성을 검사합니다.
   *
   * @param commentId 유효성 검사를 수행할 댓글의 ID
   */
  void validateComment(Long commentId);
}
