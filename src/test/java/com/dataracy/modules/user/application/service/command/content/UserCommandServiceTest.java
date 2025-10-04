package com.dataracy.modules.user.application.service.command.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageRefreshTokenUseCase;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserCommandServiceTest {

  @InjectMocks private UserCommandService service;

  @BeforeEach
  void setUp() {
    // ApplicationContext를 통한 프록시 방식으로 변경되었으므로 setSelf 호출 제거
  }

  @Mock private UserCommandPort userCommandPort;

  @Mock private UserQueryPort userQueryPort;

  @Mock private DuplicateNicknameUseCase duplicateNicknameUseCase;

  @Mock private ValidateAuthorLevelUseCase validateAuthorLevelUseCase;

  @Mock private ValidateOccupationUseCase validateOccupationUseCase;

  @Mock private ValidateVisitSourceUseCase validateVisitSourceUseCase;

  @Mock private ValidateTopicUseCase validateTopicUseCase;

  @Mock private FileCommandUseCase fileCommandUseCase;

  @Mock private JwtValidateUseCase jwtValidateUseCase;

  @Mock private ManageRefreshTokenUseCase manageRefreshTokenUseCase;

  @Mock private MultipartFile profileImageFile;

  private ModifyUserInfoRequest createSampleModifyRequest() {
    return new ModifyUserInfoRequest("testuser", 1L, 1L, List.of(1L, 2L), 1L, "Test Introduction");
  }

  @Nested
  @DisplayName("사용자 정보 수정")
  class ModifyUserInfo {

    @Test
    @DisplayName("사용자 정보 수정 성공 - 닉네임 변경")
    void modifyUserInfoSuccessWithNicknameChange() {
      // given
      Long userId = 1L;
      ModifyUserInfoRequest request = createSampleModifyRequest();
      String savedNickname = "oldsaveduser";

      // Mock MultipartFile 설정
      given(profileImageFile.isEmpty()).willReturn(false);
      given(profileImageFile.getOriginalFilename()).willReturn("profile.jpg");
      given(profileImageFile.getSize()).willReturn(1024L);

      given(userQueryPort.findNicknameById(userId))
          .willReturn(java.util.Optional.of(savedNickname));
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(userCommandPort).modifyUserInfo(anyLong(), any());
      given(fileCommandUseCase.uploadFile(anyString(), any())).willReturn("uploaded-url");

      // when
      service.modifyUserInfo(userId, profileImageFile, request);

      // then
      then(userCommandPort).should().modifyUserInfo(userId, request);
      then(fileCommandUseCase).should().uploadFile(anyString(), any());
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - 닉네임 변경 없음")
    void modifyUserInfoSuccessWithoutNicknameChange() {
      // given
      Long userId = 1L;
      ModifyUserInfoRequest request = createSampleModifyRequest();
      String savedNickname = "testuser"; // 동일한 닉네임

      // Mock MultipartFile 설정
      given(profileImageFile.isEmpty()).willReturn(false);
      given(profileImageFile.getOriginalFilename()).willReturn("profile.jpg");
      given(profileImageFile.getSize()).willReturn(1024L);

      given(userQueryPort.findNicknameById(userId))
          .willReturn(java.util.Optional.of(savedNickname));
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(userCommandPort).modifyUserInfo(anyLong(), any());
      given(fileCommandUseCase.uploadFile(anyString(), any())).willReturn("uploaded-url");

      // when
      service.modifyUserInfo(userId, profileImageFile, request);

      // then
      then(userCommandPort).should().modifyUserInfo(userId, request);
      then(duplicateNicknameUseCase).should(never()).validateDuplicatedNickname(anyString());
    }

    @Test
    @DisplayName("사용자 정보 수정 실패 - 사용자가 존재하지 않음")
    void modifyUserInfoFailUserNotFound() {
      // given
      Long userId = 999L;
      ModifyUserInfoRequest request = createSampleModifyRequest();

      given(userQueryPort.findNicknameById(userId)).willReturn(java.util.Optional.empty());

      // when & then
      UserException ex =
          catchThrowableOfType(
              () -> service.modifyUserInfo(userId, profileImageFile, request), UserException.class);
      assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }

    @Test
    @DisplayName("사용자 정보 수정 실패 - 파일 업로드 실패")
    void modifyUserInfoFailFileUploadFailure() {
      // given
      Long userId = 1L;
      ModifyUserInfoRequest request = createSampleModifyRequest();
      String savedNickname = "oldsaveduser";

      // Mock MultipartFile 설정
      given(profileImageFile.isEmpty()).willReturn(false);
      given(profileImageFile.getOriginalFilename()).willReturn("profile.jpg");
      given(profileImageFile.getSize()).willReturn(1024L);

      given(userQueryPort.findNicknameById(userId))
          .willReturn(java.util.Optional.of(savedNickname));
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(userCommandPort).modifyUserInfo(anyLong(), any());
      given(fileCommandUseCase.uploadFile(anyString(), any()))
          .willThrow(new RuntimeException("Upload failed"));

      // when & then
      CommonException ex =
          catchThrowableOfType(
              () -> service.modifyUserInfo(userId, profileImageFile, request),
              CommonException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.FILE_UPLOAD_FAILURE);
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - null 프로필 이미지")
    void modifyUserInfoSuccessWithNullProfileImage() {
      // given
      Long userId = 1L;
      ModifyUserInfoRequest request = createSampleModifyRequest();
      String savedNickname = "oldsaveduser";

      given(userQueryPort.findNicknameById(userId))
          .willReturn(java.util.Optional.of(savedNickname));
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      willDoNothing().given(validateTopicUseCase).validateTopic(anyLong());
      willDoNothing().given(userCommandPort).modifyUserInfo(anyLong(), any());

      // when
      service.modifyUserInfo(userId, null, request);

      // then
      then(userCommandPort).should().modifyUserInfo(userId, request);
      then(fileCommandUseCase).should(never()).uploadFile(anyString(), any());
    }
  }

  @Nested
  @DisplayName("사용자 탈퇴")
  class WithdrawUser {

    @Test
    @DisplayName("사용자 탈퇴 성공")
    void withdrawUserSuccess() {
      // given
      Long userId = 1L;
      willDoNothing().given(userCommandPort).withdrawalUser(userId);

      // when
      service.withdrawUser(userId);

      // then
      then(userCommandPort).should().withdrawalUser(userId);
    }

    @Test
    @DisplayName("사용자 탈퇴 성공 - 음수 사용자 ID")
    void withdrawUserSuccessWithNegativeId() {
      // given
      Long negativeUserId = -1L;
      willDoNothing().given(userCommandPort).withdrawalUser(negativeUserId);

      // when
      service.withdrawUser(negativeUserId);

      // then
      then(userCommandPort).should().withdrawalUser(negativeUserId);
    }
  }

  @Nested
  @DisplayName("사용자 로그아웃")
  class LogoutUser {

    @Test
    @DisplayName("사용자 로그아웃 성공")
    void logoutUserSuccess() {
      // given
      Long userId = 1L;
      String refreshToken = "valid-refresh-token";

      given(jwtValidateUseCase.getUserIdFromToken(refreshToken)).willReturn(userId);
      willDoNothing().given(manageRefreshTokenUseCase).deleteRefreshToken(anyString());

      // when
      service.logout(userId, refreshToken);

      // then
      then(manageRefreshTokenUseCase).should().deleteRefreshToken(String.valueOf(userId));
    }

    @Test
    @DisplayName("사용자 로그아웃 실패 - 만료된 리프레시 토큰")
    void logoutUserFailExpiredRefreshToken() {
      // given
      Long userId = 1L;
      String expiredRefreshToken = "expired-refresh-token";

      given(jwtValidateUseCase.getUserIdFromToken(expiredRefreshToken)).willReturn(null);

      // when & then
      AuthException ex =
          catchThrowableOfType(
              () -> service.logout(userId, expiredRefreshToken), AuthException.class);
      assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("사용자 로그아웃 실패 - 사용자 ID 불일치")
    void logoutUserFailUserMismatch() {
      // given
      Long userId = 1L;
      Long differentUserId = 2L;
      String refreshToken = "valid-refresh-token";

      given(jwtValidateUseCase.getUserIdFromToken(refreshToken)).willReturn(differentUserId);

      // when & then
      AuthException ex =
          catchThrowableOfType(() -> service.logout(userId, refreshToken), AuthException.class);
      assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
    }
  }

  @Nested
  @DisplayName("경계값 테스트")
  class BoundaryTests {

    @Test
    @DisplayName("null 사용자 ID로 정보 수정")
    void modifyUserInfoWithNullUserId() {
      // given
      ModifyUserInfoRequest request = createSampleModifyRequest();

      given(userQueryPort.findNicknameById(null)).willReturn(java.util.Optional.empty());

      // when & then
      UserException ex =
          catchThrowableOfType(
              () -> service.modifyUserInfo(null, profileImageFile, request), UserException.class);
      assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }

    @Test
    @DisplayName("빈 토픽 ID 리스트로 정보 수정")
    void modifyUserInfoWithEmptyTopicIds() {
      // given
      Long userId = 1L;
      ModifyUserInfoRequest request =
          new ModifyUserInfoRequest(
              "testuser",
              1L,
              1L,
              List.of(), // 빈 토픽 ID 리스트
              1L,
              "Test Introduction");
      String savedNickname = "oldsaveduser";

      given(userQueryPort.findNicknameById(userId))
          .willReturn(java.util.Optional.of(savedNickname));
      willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname(anyString());
      willDoNothing().given(validateAuthorLevelUseCase).validateAuthorLevel(anyLong());
      willDoNothing().given(validateOccupationUseCase).validateOccupation(anyLong());
      willDoNothing().given(validateVisitSourceUseCase).validateVisitSource(anyLong());
      willDoNothing().given(userCommandPort).modifyUserInfo(anyLong(), any());

      // Mock MultipartFile 설정
      given(profileImageFile.isEmpty()).willReturn(false);
      given(profileImageFile.getOriginalFilename()).willReturn("test.jpg");
      given(profileImageFile.getSize()).willReturn(1024L);

      // when
      service.modifyUserInfo(userId, profileImageFile, request);

      // then
      then(userCommandPort).should().modifyUserInfo(userId, request);
      then(validateTopicUseCase).should(never()).validateTopic(anyLong());
    }
  }
}
