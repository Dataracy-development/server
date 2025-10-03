/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.application.service.query.extractor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.comment.application.dto.response.support.CommentLabelResponse;
import com.dataracy.modules.comment.application.port.in.query.extractor.FindCommentUserInfoUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserAuthorLevelIdsUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;

import lombok.RequiredArgsConstructor;

/** 댓글 조회 시 필요한 사용자 정보를 배치로 조회하는 서비스 */
@Service
@RequiredArgsConstructor
public class CommentUserInfoService implements FindCommentUserInfoUseCase {

  private final FindUsernameUseCase findUsernameUseCase;
  private final FindUserThumbnailUseCase findUserThumbnailUseCase;
  private final FindUserAuthorLevelIdsUseCase findUserAuthorLevelIdsUseCase;
  private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

  // Use Case 상수 정의
  private static final String FIND_COMMENT_USER_INFO_USE_CASE = "FindCommentUserInfoUseCase";

  /**
   * 주어진 사용자 ID 목록에 대해 댓글 표시에 필요한 사용자 정보를 배치로 조회합니다.
   *
   * <p>N+1 문제를 방지하기 위해 모든 사용자 정보를 배치로 조회합니다.
   *
   * @param userIds 정보 조회 대상인 사용자 ID 목록
   * @return 각 사용자 ID에 대한 사용자명, 사용자 프로필 이미지 URL, 작성자 레벨 ID, 작성자 레벨 라벨이 포함된 CommentLabelResponse 객체
   */
  @Override
  @Transactional(readOnly = true)
  public CommentLabelResponse findCommentUserInfoBatch(List<Long> userIds) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                FIND_COMMENT_USER_INFO_USE_CASE, "댓글 사용자 정보 배치 조회 시작 userIds=" + userIds.size());

    if (userIds.isEmpty()) {
      LoggerFactory.service()
          .logSuccess(FIND_COMMENT_USER_INFO_USE_CASE, "댓글 사용자 정보 배치 조회 완료 (빈 목록)", startTime);
      return new CommentLabelResponse(Map.of(), Map.of(), Map.of(), Map.of());
    }

    // 중복 제거된 사용자 ID 목록으로 배치 조회
    List<Long> distinctUserIds = userIds.stream().distinct().toList();

    // 배치로 사용자 정보 조회 (각각이 이미 배치 처리됨)
    Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(distinctUserIds);
    Map<Long, String> userThumbnailMap =
        findUserThumbnailUseCase.findUserThumbnailsByIds(distinctUserIds);
    Map<Long, String> userAuthorLevelIds =
        findUserAuthorLevelIdsUseCase.findUserAuthorLevelIds(distinctUserIds);

    // 작성자 레벨 ID 목록 추출 및 배치 조회
    List<Long> authorLevelIds =
        userAuthorLevelIds.values().stream().map(Long::parseLong).distinct().toList();

    Map<Long, String> userAuthorLevelLabelMap =
        getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

    CommentLabelResponse result =
        new CommentLabelResponse(
            usernameMap, userThumbnailMap, userAuthorLevelIds, userAuthorLevelLabelMap);

    LoggerFactory.service()
        .logSuccess(
            FIND_COMMENT_USER_INFO_USE_CASE,
            "댓글 사용자 정보 배치 조회 완료 userIds=" + distinctUserIds.size(),
            startTime);
    return result;
  }
}
