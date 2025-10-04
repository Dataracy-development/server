package com.dataracy.modules.comment.application.port.out.validate;

public interface ValidateCommentPort {
  /**
   * 지정된 댓글 ID에 해당하는 댓글이 존재하는지 여부를 반환합니다.
   *
   * @param commentId 존재 여부를 확인할 댓글의 ID
   * @return 댓글이 존재하면 true, 존재하지 않으면 false
   */
  boolean existsByCommentId(Long commentId);
}
