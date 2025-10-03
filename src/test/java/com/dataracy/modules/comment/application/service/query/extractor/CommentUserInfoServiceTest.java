/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.comment.application.service.query.extractor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.comment.application.dto.response.support.CommentLabelResponse;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserAuthorLevelIdsUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentUserInfoServiceTest {

  @Mock private FindUsernameUseCase findUsernameUseCase;

  @Mock private FindUserThumbnailUseCase findUserThumbnailUseCase;

  @Mock private FindUserAuthorLevelIdsUseCase findUserAuthorLevelIdsUseCase;

  @Mock private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

  @InjectMocks private CommentUserInfoService commentUserInfoService;

  @Nested
  @DisplayName("댓글 사용자 정보 배치 조회")
  class FindCommentUserInfoBatch {

    @Test
    @DisplayName("사용자 정보 배치 조회 성공")
    void findCommentUserInfoBatchSuccess() {
      // given
      List<Long> userIds = List.of(1L, 2L, 3L);
      Map<Long, String> usernameMap =
          Map.of(
              1L, "user1",
              2L, "user2",
              3L, "user3");
      Map<Long, String> thumbnailMap =
          Map.of(
              1L, "thumb1.png",
              2L, "thumb2.png",
              3L, "thumb3.png");
      Map<Long, String> authorLevelIdsMap =
          Map.of(
              1L, "10",
              2L, "20",
              3L, "10");
      Map<Long, String> authorLevelLabelMap =
          Map.of(
              10L, "Beginner",
              20L, "Intermediate");

      given(findUsernameUseCase.findUsernamesByIds(userIds)).willReturn(usernameMap);
      given(findUserThumbnailUseCase.findUserThumbnailsByIds(userIds)).willReturn(thumbnailMap);
      given(findUserAuthorLevelIdsUseCase.findUserAuthorLevelIds(userIds))
          .willReturn(authorLevelIdsMap);
      given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(any())).willReturn(authorLevelLabelMap);

      // when
      CommentLabelResponse result = commentUserInfoService.findCommentUserInfoBatch(userIds);

      // then
      assertAll(
          () -> assertThat(result.usernameMap()).isEqualTo(usernameMap),
          () -> assertThat(result.userProfileUrlMap()).isEqualTo(thumbnailMap),
          () -> assertThat(result.userAuthorLevelIds()).isEqualTo(authorLevelIdsMap),
          () -> assertThat(result.userAuthorLevelLabelMap()).isEqualTo(authorLevelLabelMap));

      then(findUsernameUseCase).should().findUsernamesByIds(userIds);
      then(findUserThumbnailUseCase).should().findUserThumbnailsByIds(userIds);
      then(findUserAuthorLevelIdsUseCase).should().findUserAuthorLevelIds(userIds);
      then(getAuthorLevelLabelFromIdUseCase).should().getLabelsByIds(any());
    }

    @Test
    @DisplayName("빈 사용자 ID 목록으로 조회")
    void findCommentUserInfoBatchEmptyList() {
      // given
      List<Long> userIds = List.of();

      // when
      CommentLabelResponse result = commentUserInfoService.findCommentUserInfoBatch(userIds);

      // then
      assertAll(
          () -> assertThat(result.usernameMap()).isEmpty(),
          () -> assertThat(result.userProfileUrlMap()).isEmpty(),
          () -> assertThat(result.userAuthorLevelIds()).isEmpty(),
          () -> assertThat(result.userAuthorLevelLabelMap()).isEmpty());

      // 빈 목록일 때는 다른 서비스들을 호출하지 않음
      then(findUsernameUseCase).shouldHaveNoInteractions();
      then(findUserThumbnailUseCase).shouldHaveNoInteractions();
      then(findUserAuthorLevelIdsUseCase).shouldHaveNoInteractions();
      then(getAuthorLevelLabelFromIdUseCase).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("중복된 사용자 ID가 있는 경우 중복 제거 후 조회")
    void findCommentUserInfoBatchWithDuplicates() {
      // given
      List<Long> userIds = List.of(1L, 2L, 1L, 3L, 2L); // 중복 포함
      List<Long> distinctUserIds = List.of(1L, 2L, 3L); // 중복 제거된 목록
      Map<Long, String> usernameMap =
          Map.of(
              1L, "user1",
              2L, "user2",
              3L, "user3");
      Map<Long, String> thumbnailMap =
          Map.of(
              1L, "thumb1.png",
              2L, "thumb2.png",
              3L, "thumb3.png");
      Map<Long, String> authorLevelIdsMap =
          Map.of(
              1L, "10",
              2L, "20",
              3L, "30");
      Map<Long, String> authorLevelLabelMap =
          Map.of(
              10L, "Beginner",
              20L, "Intermediate",
              30L, "Advanced");

      given(findUsernameUseCase.findUsernamesByIds(any())).willReturn(usernameMap);
      given(findUserThumbnailUseCase.findUserThumbnailsByIds(any())).willReturn(thumbnailMap);
      given(findUserAuthorLevelIdsUseCase.findUserAuthorLevelIds(any()))
          .willReturn(authorLevelIdsMap);
      given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(any())).willReturn(authorLevelLabelMap);

      // when
      CommentLabelResponse result = commentUserInfoService.findCommentUserInfoBatch(userIds);

      // then
      assertAll(
          () -> assertThat(result.usernameMap()).isEqualTo(usernameMap),
          () -> assertThat(result.userProfileUrlMap()).isEqualTo(thumbnailMap),
          () -> assertThat(result.userAuthorLevelIds()).isEqualTo(authorLevelIdsMap),
          () -> assertThat(result.userAuthorLevelLabelMap()).isEqualTo(authorLevelLabelMap));

      // 중복 제거된 목록으로 호출됨
      then(findUsernameUseCase).should().findUsernamesByIds(distinctUserIds);
      then(findUserThumbnailUseCase).should().findUserThumbnailsByIds(distinctUserIds);
      then(findUserAuthorLevelIdsUseCase).should().findUserAuthorLevelIds(distinctUserIds);
      then(getAuthorLevelLabelFromIdUseCase).should().getLabelsByIds(any());
    }

    @Test
    @DisplayName("일부 사용자 정보가 없는 경우")
    void findCommentUserInfoBatchPartialData() {
      // given
      List<Long> userIds = List.of(1L, 2L, 3L);
      Map<Long, String> usernameMap =
          Map.of(
              1L, "user1",
              2L, "user2"
              // 3L 사용자 정보 없음
              );
      Map<Long, String> thumbnailMap =
          Map.of(
              1L, "thumb1.png"
              // 2L, 3L 썸네일 정보 없음
              );
      Map<Long, String> authorLevelIdsMap =
          Map.of(
              1L, "10",
              2L, "20"
              // 3L 작성자 레벨 정보 없음
              );
      Map<Long, String> authorLevelLabelMap =
          Map.of(
              10L, "Beginner",
              20L, "Intermediate");

      given(findUsernameUseCase.findUsernamesByIds(any())).willReturn(usernameMap);
      given(findUserThumbnailUseCase.findUserThumbnailsByIds(any())).willReturn(thumbnailMap);
      given(findUserAuthorLevelIdsUseCase.findUserAuthorLevelIds(any()))
          .willReturn(authorLevelIdsMap);
      given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(any())).willReturn(authorLevelLabelMap);

      // when
      CommentLabelResponse result = commentUserInfoService.findCommentUserInfoBatch(userIds);

      // then
      assertAll(
          () -> assertThat(result.usernameMap()).isEqualTo(usernameMap),
          () -> assertThat(result.userProfileUrlMap()).isEqualTo(thumbnailMap),
          () -> assertThat(result.userAuthorLevelIds()).isEqualTo(authorLevelIdsMap),
          () -> assertThat(result.userAuthorLevelLabelMap()).isEqualTo(authorLevelLabelMap));
    }
  }
}
