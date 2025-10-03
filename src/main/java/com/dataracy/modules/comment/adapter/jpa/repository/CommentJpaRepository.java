/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.adapter.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dataracy.modules.comment.adapter.jpa.entity.CommentEntity;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
  /**
   * 주어진 댓글 ID에 해당하는 사용자의 ID를 조회합니다.
   *
   * @param commentId 조회할 댓글의 ID
   * @return 댓글 작성자의 사용자 ID를 포함하는 Optional 객체. 해당 댓글이 없으면 빈 Optional을 반환합니다.
   */
  @Query("select c.userId from CommentEntity c where c.id = :commentId")
  Optional<Long> findUserIdById(@Param("commentId") Long commentId);

  /**
   * 지정된 댓글의 좋아요 수를 1 증가시킵니다.
   *
   * @param commentId 좋아요 수를 증가시킬 댓글의 ID
   */
  @Modifying
  @Query("UPDATE CommentEntity c SET c.likeCount = c.likeCount + 1 WHERE c.id = :commentId")
  void increaseLikeCount(@Param("commentId") Long commentId);

  /**
   * 지정된 댓글의 좋아요 수를 1 감소시킵니다. 좋아요 수가 0 이하인 경우 0으로 유지됩니다.
   *
   * @param commentId 좋아요 수를 감소시킬 댓글의 ID
   */
  @Modifying
  @Query(
      """
    UPDATE CommentEntity c
    SET c.likeCount =
        CASE
            WHEN c.likeCount > 0 THEN c.likeCount - 1
            ELSE 0
        END
    WHERE c.id = :commentId
    """)
  void decreaseLikeCount(@Param("commentId") Long commentId);
}
