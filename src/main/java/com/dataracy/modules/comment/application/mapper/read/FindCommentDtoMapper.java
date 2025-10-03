/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.application.mapper.read;

import org.springframework.stereotype.Component;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.domain.model.Comment;

/** 댓글 도메인 모델과 애플리케이션 응답 DTO를 변환하는 매퍼 */
@Component
public class FindCommentDtoMapper {
  /**
   * 댓글 도메인과 사용자 관련 메타데이터를 결합해 FindCommentResponse DTO로 변환합니다.
   *
   * @param comment 변환할 댓글 도메인 객체
   * @param username 댓글 작성자의 사용자명
   * @param userProfileImageUrl 댓글 작성자의 프로필 이미지 URL
   * @param authorLevelLabel 댓글 작성자의 레벨 라벨
   * @param childCommentCount 해당 댓글의 자식(답글) 수
   * @param isLiked 호출 사용자 기준으로 해당 댓글에 좋아요가 눌렸는지 여부
   * @return 댓글 정보, 작성자 정보, 자식 댓글 수 및 좋아요 여부를 포함한 FindCommentResponse
   */
  public FindCommentResponse toResponseDto(
      Comment comment,
      String username,
      String userProfileImageUrl,
      String authorLevelLabel,
      Long childCommentCount,
      boolean isLiked) {
    return new FindCommentResponse(
        comment.getId(),
        comment.getUserId(),
        username,
        userProfileImageUrl,
        authorLevelLabel,
        comment.getContent(),
        comment.getLikeCount(),
        childCommentCount,
        comment.getCreatedAt(),
        isLiked);
  }

  /**
   * 답글 도메인 객체와 관련 사용자 메타데이터를 FindReplyCommentResponse DTO로 변환합니다.
   *
   * <p>Comment의 id, userId, content, likeCount, createdAt 값을 추출해 전달된 작성자 정보(username,
   * userProfileImageUrl, authorLevelLabel)와 isLiked 플래그와 함께 DTO로 생성합니다.
   *
   * @param comment 변환할 답글 도메인 객체
   * @param username 답글 작성자의 사용자명
   * @param userProfileImageUrl 답글 작성자의 프로필 이미지 URL
   * @param authorLevelLabel 답글 작성자의 등급 라벨
   * @param isLiked 호출 사용자 기준으로 해당 답글에 좋아요 여부
   * @return 답글 정보를 담은 FindReplyCommentResponse 객체
   */
  public FindReplyCommentResponse toResponseDto(
      Comment comment,
      String username,
      String userProfileImageUrl,
      String authorLevelLabel,
      boolean isLiked) {
    return new FindReplyCommentResponse(
        comment.getId(),
        comment.getUserId(),
        username,
        userProfileImageUrl,
        authorLevelLabel,
        comment.getContent(),
        comment.getLikeCount(),
        comment.getCreatedAt(),
        isLiked);
  }
}
