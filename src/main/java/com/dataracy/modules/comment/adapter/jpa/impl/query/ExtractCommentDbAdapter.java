package com.dataracy.modules.comment.adapter.jpa.impl.query;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import com.dataracy.modules.comment.application.port.out.query.extractor.ExtractCommentPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExtractCommentDbAdapter implements ExtractCommentPort {
  private final CommentJpaRepository commentJpaRepository;

  // Entity 상수 정의
  private static final String COMMENT_ENTITY = "CommentEntity";

  /**
   * 주어진 댓글 ID에 해당하는 사용자 ID를 Optional로 반환합니다.
   *
   * @param commentId 사용자 ID를 조회할 댓글의 ID
   * @return 댓글에 연결된 사용자 ID의 Optional, 해당 댓글이 없으면 빈 Optional
   */
  @Override
  public Optional<Long> findUserIdByCommentId(Long commentId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(
                COMMENT_ENTITY,
                "[findUserIdById] 주어진 댓글 ID에 연결된 사용자 ID 조회 시작 commentId=" + commentId);
    Optional<Long> userId = commentJpaRepository.findUserIdById(commentId);
    LoggerFactory.db()
        .logQueryEnd(
            COMMENT_ENTITY,
            "[findUserIdById] 주어진 댓글 ID에 연결된 사용자 ID 조회 종료 commentId=" + commentId,
            startTime);
    return userId;
  }
}
