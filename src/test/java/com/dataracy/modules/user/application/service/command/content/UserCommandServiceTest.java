package com.dataracy.modules.user.application.service.command.content;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.BlackListTokenUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageRefreshTokenUseCase;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.ValidateOccupationUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.ValidateVisitSourceUseCase;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @Mock
    private UserCommandPort userCommandPort;

    @Mock
    private com.dataracy.modules.user.application.port.out.query.UserQueryPort userQueryPort;

    @Mock
    private DuplicateNicknameUseCase duplicateNicknameUseCase;

    @Mock
    private ValidateAuthorLevelUseCase validateAuthorLevelUseCase;

    @Mock
    private ValidateOccupationUseCase validateOccupationUseCase;

    @Mock
    private ValidateVisitSourceUseCase validateVisitSourceUseCase;

    @Mock
    private ValidateTopicUseCase validateTopicUseCase;

    @Mock
    private FileCommandUseCase fileCommandUseCase;

    @Mock
    private MultipartFile profileImageFile;

    @Mock
    private JwtValidateUseCase jwtValidateUseCase;

    @Mock
    private ManageRefreshTokenUseCase manageRefreshTokenUseCase;

    @InjectMocks
    private UserCommandService service;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Nested
    @DisplayName("modifyUserInfo")
    class ModifyUserInfo {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("프로필 이미지 없이 회원정보 수정 성공")
            void successWithoutImage() {
                // given
                Long userId = 1L;
                ModifyUserInfoRequest req = new ModifyUserInfoRequest(
                        "닉네임", 2L, 3L, List.of(10L, 20L), 4L, "자기소개"
                );
                MultipartFile nullImageFile = null;
                
                // Mock 설정
                given(userQueryPort.findNicknameById(userId)).willReturn(java.util.Optional.of("기존닉네임"));

                // when
                service.modifyUserInfo(userId, nullImageFile, req);

                // then
                then(duplicateNicknameUseCase).should().validateDuplicatedNickname("닉네임");
                then(validateAuthorLevelUseCase).should().validateAuthorLevel(2L);
                then(validateOccupationUseCase).should().validateOccupation(3L);
                then(validateVisitSourceUseCase).should().validateVisitSource(4L);
                then(validateTopicUseCase).should(times(2)).validateTopic(anyLong());

                then(userCommandPort).should().modifyUserInfo(userId, req);
                then(userCommandPort).should(never()).updateProfileImageFile(anyLong(), anyString());
            }

            @Test
            @DisplayName("프로필 이미지와 함께 회원정보 수정 성공")
            void successWithImage() {
                // given
                Long userId = 1L;
                ModifyUserInfoRequest req = new ModifyUserInfoRequest(
                        "닉네임", 2L, null, List.of(), null, "소개"
                );
                given(profileImageFile.isEmpty()).willReturn(false);
                given(profileImageFile.getOriginalFilename()).willReturn("profile.png");
                given(fileCommandUseCase.uploadFile(anyString(), eq(profileImageFile)))
                        .willReturn("https://s3.bucket/profile.png");
                
                // Mock 설정
                given(userQueryPort.findNicknameById(userId)).willReturn(java.util.Optional.of("기존닉네임"));

                // when
                service.modifyUserInfo(userId, profileImageFile, req);

                // then
                then(userCommandPort).should().modifyUserInfo(userId, req);
                then(fileCommandUseCase).should().uploadFile(stringCaptor.capture(), eq(profileImageFile));
                then(userCommandPort).should().updateProfileImageFile(eq(userId), eq("https://s3.bucket/profile.png"));

                assertThat(stringCaptor.getValue()).contains("user/" + userId);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("닉네임 중복 시 예외 발생")
            void failDuplicatedNickname() {
                // given
                Long userId = 1L;
                ModifyUserInfoRequest req = new ModifyUserInfoRequest(
                        "중복닉", 2L, null, List.of(10L), 5L, "소개"
                );
                
                // Mock 설정
                given(userQueryPort.findNicknameById(userId)).willReturn(java.util.Optional.of("기존닉네임"));

                willThrow(new IllegalArgumentException("닉네임 중복"))
                        .given(duplicateNicknameUseCase).validateDuplicatedNickname("중복닉");

                // when
                IllegalArgumentException ex = catchThrowableOfType(
                        () -> service.modifyUserInfo(userId, null, req),
                        IllegalArgumentException.class
                );

                // then
                assertThat(ex).isNotNull();
                assertThat(ex.getMessage()).isEqualTo("닉네임 중복");
            }

            @Test
            @DisplayName("이미지 파일 검증 실패 시 예외 발생")
            void failInvalidImage() {
                // given
                Long userId = 1L;
                ModifyUserInfoRequest req = new ModifyUserInfoRequest(
                        "닉네임", 2L, null, List.of(20L), null,"소개"
                );
                
                // Mock 설정
                given(userQueryPort.findNicknameById(userId)).willReturn(java.util.Optional.of("기존닉네임"));

                willThrow(new IllegalArgumentException("잘못된 이미지"))
                        .given(profileImageFile).getOriginalFilename();

                // when
                IllegalArgumentException ex = catchThrowableOfType(
                        () -> service.modifyUserInfo(userId, profileImageFile, req),
                        IllegalArgumentException.class
                );

                // then
                assertThat(ex).isNotNull();
                assertThat(ex.getMessage()).isEqualTo("잘못된 이미지");
            }
        }
    }

    @Nested
    @DisplayName("withdrawUser")
    class WithdrawUser {

        @Test
        @DisplayName("회원 탈퇴 성공")
        void success() {
            // given
            Long userId = 1L;

            // when
            service.withdrawUser(userId);

            // then
            then(userCommandPort).should().withdrawalUser(userId);
        }
    }

    @Nested
    @DisplayName("logout")
    class Logout {

        @Test
        @DisplayName("성공: 유저의 refreshToken 삭제")
        void success() {
            // given
            Long userId = 1L;
            String refreshToken = "valid-refresh-token";
            given(jwtValidateUseCase.getUserIdFromToken(refreshToken)).willReturn(userId);

            // when
            service.logout(userId, refreshToken);

            // then
            then(jwtValidateUseCase).should().getUserIdFromToken(refreshToken);
            then(manageRefreshTokenUseCase).should().deleteRefreshToken(String.valueOf(userId));
        }

        @Test
        @DisplayName("실패: 만료된 리프레시 토큰일 경우 AuthException(EXPIRED_REFRESH_TOKEN) 발생")
        void fail_expiredToken() {
            // given
            Long userId = 1L;
            String refreshToken = "expired-refresh-token";
            given(jwtValidateUseCase.getUserIdFromToken(refreshToken)).willReturn(null);

            // when
            AuthException ex = catchThrowableOfType(() -> service.logout(userId, refreshToken), AuthException.class);

            // then
            assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
            then(manageRefreshTokenUseCase).should(never()).deleteRefreshToken(anyString());
        }

        @Test
        @DisplayName("실패: 토큰의 userId와 요청 userId가 다를 경우 AuthException(REFRESH_TOKEN_USER_MISMATCH_IN_REDIS) 발생")
        void fail_userMismatch() {
            // given
            Long userId = 1L;
            String refreshToken = "other-user-refresh-token";
            given(jwtValidateUseCase.getUserIdFromToken(refreshToken)).willReturn(2L);

            // when
            AuthException ex = catchThrowableOfType(() -> service.logout(userId, refreshToken), AuthException.class);

            // then
            assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.REFRESH_TOKEN_USER_MISMATCH_IN_REDIS);
            then(manageRefreshTokenUseCase).should(never()).deleteRefreshToken(anyString());
        }
    }
}
