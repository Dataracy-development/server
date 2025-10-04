package com.dataracy.modules.user.application.service.query.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.GetVisitSourceLabelFromIdUseCase;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.application.port.out.query.UserMultiQueryPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

  @Mock private UserQueryPort userQueryPort;

  @Mock private UserMultiQueryPort userMultiQueryPort;

  @Mock private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;

  @Mock private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

  @Mock private GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;

  @Mock private GetVisitSourceLabelFromIdUseCase getVisitSourceLabelFromIdUseCase;

  @InjectMocks private UserProfileService service;

  private User localUser() {
    return User.builder()
        .id(10L)
        .provider(ProviderType.LOCAL)
        .providerId("local-10")
        .role(RoleType.ROLE_USER)
        .email("u@test.com")
        .password("encoded")
        .nickname("nick")
        .authorLevelId(1L)
        .occupationId(2L)
        .topicIds(List.of(100L, 200L))
        .visitSourceId(3L)
        .profileImageUrl("img.png")
        .introductionText("intro")
        .isDeleted(false)
        .build();
  }

  // ---------------- findUsernameById ----------------
  @Nested
  class FindUsernameByIdTests {

    @Test
    @DisplayName("유저 존재 → 닉네임 반환")
    void findUsernameByIdSuccess() {
      // given
      given(userQueryPort.findUserById(10L)).willReturn(Optional.of(localUser()));

      // when
      String nickname = service.findUsernameById(10L);

      // then
      assertThat(nickname).isEqualTo("nick");
    }

    @Test
    @DisplayName("유저 없음 → NOT_FOUND_USER 예외")
    void findUsernameByIdNotFound() {
      // given
      given(userQueryPort.findUserById(10L)).willReturn(Optional.empty());

      // when
      UserException ex =
          catchThrowableOfType(() -> service.findUsernameById(10L), UserException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }
  }

  // ---------------- findUsernamesByIds ----------------
  @Nested
  class FindUsernamesByIdsTests {

    @Test
    @DisplayName("null/빈 리스트 → 빈 Map 반환")
    void findUsernamesByIdsEmpty() {
      assertAll(
          () -> assertThat(service.findUsernamesByIds(null)).isEmpty(),
          () -> assertThat(service.findUsernamesByIds(List.of())).isEmpty());
    }

    @Test
    @DisplayName("유저들 존재 → Map 반환")
    void findUsernamesByIdsSuccess() {
      // given
      Map<Long, String> expected = Map.of(1L, "a", 2L, "b");
      given(userMultiQueryPort.findUsernamesByIds(List.of(1L, 2L))).willReturn(expected);

      // when
      Map<Long, String> res = service.findUsernamesByIds(List.of(1L, 2L));

      // then
      assertThat(res).containsEntry(1L, "a").containsEntry(2L, "b");
    }
  }

  // ---------------- findUserThumbnailsByIds ----------------
  @Nested
  class FindUserThumbnailsByIdsTests {

    @Test
    @DisplayName("null/빈 리스트 → 빈 Map 반환")
    void findUserThumbnailsByIdsEmpty() {
      assertAll(
          () -> assertThat(service.findUserThumbnailsByIds(null)).isEmpty(),
          () -> assertThat(service.findUserThumbnailsByIds(List.of())).isEmpty());
    }

    @Test
    @DisplayName("유저 썸네일 반환")
    void findUserThumbnailsByIdsSuccess() {
      // given
      Map<Long, String> expected = Map.of(1L, "img1", 2L, "img2");
      given(userMultiQueryPort.findUserThumbnailsByIds(List.of(1L, 2L))).willReturn(expected);

      // when
      Map<Long, String> res = service.findUserThumbnailsByIds(List.of(1L, 2L));

      // then
      assertThat(res).containsEntry(1L, "img1").containsEntry(2L, "img2");
    }
  }

  // ---------------- findUserAuthorLevelIds ----------------
  @Nested
  class FindUserAuthorLevelIdsTests {

    @Test
    @DisplayName("null/빈 리스트 → 빈 Map 반환")
    void findUserAuthorLevelIdsEmpty() {
      assertAll(
          () -> assertThat(service.findUserAuthorLevelIds(null)).isEmpty(),
          () -> assertThat(service.findUserAuthorLevelIds(List.of())).isEmpty());
    }

    @Test
    @DisplayName("유저 레벨 반환")
    void findUserAuthorLevelIdsSuccess() {
      // given
      Map<Long, String> expected = Map.of(1L, "lv1", 2L, "lv2");
      given(userMultiQueryPort.findUserAuthorLevelIds(List.of(1L, 2L))).willReturn(expected);

      // when
      Map<Long, String> res = service.findUserAuthorLevelIds(List.of(1L, 2L));

      // then
      assertThat(res).containsEntry(1L, "lv1").containsEntry(2L, "lv2");
    }
  }

  // ---------------- extractUserInfo ----------------
  @Test
  @DisplayName("extractUserInfo: 유저 존재 → UserInfo 반환")
  void extractUserInfoSuccess() {
    // given
    given(userQueryPort.findUserById(10L)).willReturn(Optional.of(localUser()));

    // when
    UserInfo res = service.extractUserInfo(10L);

    // then
    assertAll(
        () -> assertThat(res.email()).isEqualTo("u@test.com"),
        () -> assertThat(res.nickname()).isEqualTo("nick"));
  }

  @Test
  @DisplayName("extractUserInfo: 유저 없음 → NOT_FOUND_USER 예외")
  void extractUserInfoNotFound() {
    // given
    given(userQueryPort.findUserById(10L)).willReturn(Optional.empty());

    // when
    UserException ex =
        catchThrowableOfType(() -> service.extractUserInfo(10L), UserException.class);

    // then
    assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
  }

  // ---------------- getUserInfo ----------------
  @Test
  @DisplayName("getUserInfo: 유저 존재 → 상세정보 반환")
  void getUserInfoSuccess() {
    // given
    User user = localUser();
    given(userQueryPort.findUserById(10L)).willReturn(Optional.of(user));
    given(getAuthorLevelLabelFromIdUseCase.getLabelById(1L)).willReturn("Author");
    given(getOccupationLabelFromIdUseCase.getLabelById(2L)).willReturn("Dev");
    given(getTopicLabelFromIdUseCase.getLabelsByIds(user.getTopicIds()))
        .willReturn(Map.of(100L, "TopicA", 200L, "TopicB"));
    given(getVisitSourceLabelFromIdUseCase.getLabelById(3L)).willReturn("SNS");

    // when
    GetUserInfoResponse res = service.getUserInfo(10L);

    // then
    assertAll(
        () -> assertThat(res.nickname()).isEqualTo("nick"),
        () -> assertThat(res.authorLevelLabel()).isEqualTo("Author"),
        () -> assertThat(res.occupationLabel()).isEqualTo("Dev"),
        () -> assertThat(res.topicLabels()).containsExactly("TopicA", "TopicB"),
        () -> assertThat(res.visitSourceLabel()).isEqualTo("SNS"));
  }

  @Test
  @DisplayName("getUserInfo: 유저 없음 → NOT_FOUND_USER 예외")
  void getUserInfoNotFound() {
    // given
    given(userQueryPort.findUserById(10L)).willReturn(Optional.empty());

    // when
    UserException ex = catchThrowableOfType(() -> service.getUserInfo(10L), UserException.class);

    // then
    assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
  }
}
