/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.adapter.web.api.read;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dataracy.modules.comment.adapter.web.mapper.read.ReadCommentWebMapper;
import com.dataracy.modules.comment.adapter.web.response.read.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.read.FindReplyCommentWebResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.port.in.query.read.FindCommentListUseCase;
import com.dataracy.modules.comment.application.port.in.query.read.FindReplyCommentListUseCase;
import com.dataracy.modules.comment.domain.status.CommentSuccessStatus;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.ExtractHeaderUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentReadController implements CommentReadApi {
  private final ExtractHeaderUtil extractHeaderUtil;

  private final ReadCommentWebMapper readCommentWebMapper;

  private final FindCommentListUseCase findCommentListUseCase;
  private final FindReplyCommentListUseCase findReplyCommentListUseCase;

  /**
   * 지정한 프로젝트의 댓글 목록을 페이지네이션하여 조회합니다. 인증된 사용자의 정보를 반영하여 프로젝트에 속한 댓글 목록을 반환합니다.
   *
   * @param request 인증된 사용자 정보를 추출할 HTTP 요청 객체
   * @param projectId 댓글을 조회할 프로젝트의 ID
   * @param pageable 페이지네이션 및 정렬 정보
   * @return 조회된 댓글 목록과 성공 상태가 포함된 HTTP 200 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<Page<FindCommentWebResponse>>> findComments(
      HttpServletRequest request, Long projectId, Pageable pageable) {
    Instant startTime = LoggerFactory.api().logRequest("[FindComments] 프로젝트 댓글 목록 조회 API 요청 시작");
    Page<FindCommentWebResponse> webResponse;

    try {
      Long userId = extractHeaderUtil.extractAuthenticatedUserIdFromRequest(request);

      Page<FindCommentResponse> responseDto =
          findCommentListUseCase.findComments(userId, projectId, pageable);
      webResponse = responseDto.map(readCommentWebMapper::toWebDto);
    } finally {
      LoggerFactory.api().logResponse("[FindComments] 프로젝트 댓글 목록 조회 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(CommentSuccessStatus.GET_COMMENTS, webResponse));
  }

  /**
   * 프로젝트 내 특정 댓글에 대한 답글 목록을 페이지 단위로 조회하여 반환합니다. 인증된 사용자의 정보를 기반으로, 지정된 프로젝트와 댓글에 속한 답글들을 페이징 처리하여
   * 제공합니다.
   *
   * @param request 인증된 사용자 정보를 포함하는 HTTP 요청 객체
   * @param projectId 답글을 조회할 프로젝트의 식별자
   * @param commentId 답글을 조회할 대상 댓글의 식별자
   * @param pageable 페이징 및 정렬 정보
   * @return 답글 목록과 성공 상태가 포함된 HTTP 200 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<Page<FindReplyCommentWebResponse>>> findReplyComments(
      HttpServletRequest request, Long projectId, Long commentId, Pageable pageable) {
    Instant startTime =
        LoggerFactory.api().logRequest("[FindReplyComments] 댓글에 대한 답글 목록 조회 API 요청 시작");
    Page<FindReplyCommentWebResponse> webResponse;

    try {
      Long userId = extractHeaderUtil.extractAuthenticatedUserIdFromRequest(request);

      Page<FindReplyCommentResponse> responseDto =
          findReplyCommentListUseCase.findReplyComments(userId, projectId, commentId, pageable);
      webResponse = responseDto.map(readCommentWebMapper::toWebDto);
    } finally {
      LoggerFactory.api().logResponse("[FindReplyComments] 댓글에 대한 답글 목록 조회 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(CommentSuccessStatus.GET_REPLY_COMMENTS, webResponse));
  }
}
