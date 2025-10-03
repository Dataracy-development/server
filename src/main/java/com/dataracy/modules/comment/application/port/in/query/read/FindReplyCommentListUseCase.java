/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.application.port.in.query.read;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;

public interface FindReplyCommentListUseCase {
  /**
   * 특정 프로젝트 내의 댓글에 대한 답글 목록을 페이지 단위로 조회합니다. 해당 유저가 답글을 좋아요 했는지 여부를 함께 판단한다.
   *
   * @param userId 답글 목록을 조회하는 사용자의 ID
   * @param projectId 답글을 조회할 댓글이 속한 프로젝트의 ID
   * @param commentId 답글을 조회할 대상 댓글의 ID
   * @param pageable 페이지네이션 정보를 담은 객체
   * @return 답글 목록과 페이지 정보를 포함하는 Page 객체
   */
  Page<FindReplyCommentResponse> findReplyComments(
      Long userId, Long projectId, Long commentId, Pageable pageable);
}
