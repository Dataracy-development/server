package com.dataracy.modules.comment.application.port.out.command.create;

import com.dataracy.modules.comment.domain.model.Comment;

public interface UploadCommentPort {
  /**
   * 새로운 댓글을 저장한 후, 저장된 댓글 객체를 반환합니다.
   *
   * @param comment 저장할 댓글 객체
   * @return 저장이 완료된 댓글 객체
   */
  Comment uploadComment(Comment comment);
}
