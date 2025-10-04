package com.dataracy.modules.comment.application.port.in.query.extractor;

public interface FindUserIdByCommentIdUseCase {
  /**
   * 주어진 댓글 ID를 작성한 작성자 ID를 반환합니다.
   *
   * @param commentId 조회할 댓글의 ID
   * @return 해당 댓글을 작성한 사용자의 ID
   */
  Long findUserIdByCommentId(Long commentId);
}
