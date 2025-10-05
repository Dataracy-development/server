package com.dataracy.modules.comment.adapter.jpa.impl.validate;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import com.dataracy.modules.comment.application.port.out.validate.ValidateCommentPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ValidateCommentDbAdapter implements ValidateCommentPort {
  private final CommentJpaRepository commentJpaRepository;

  // Entity 상수 정의
  private static final String COMMENT_ENTITY = "CommentEntity";

  /**
   * 주어진 댓글 ID로 댓글의 존재 여부를 확인합니다.
   *
   * @param commentId 확인할 댓글의 ID
   * @return 댓글이 존재하면 true, 존재하지 않으면 false
   */
  @Override
  public boolean existsByCommentId(Long commentId) {
    boolean isExist = commentJpaRepository.existsById(commentId);
    LoggerFactory.db()
        .logExist(
            COMMENT_ENTITY, "댓글 존재 유무 확인이 완료되었습니다. commentId=" + commentId + ", exists=" + isExist);
    return isExist;
  }
}
