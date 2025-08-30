package com.dataracy.modules.user.application.service.command.content;

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
                given(profileImageFile.isEmpty()).willReturn(true);

                // when
                service.modifyUserInfo(userId, profileImageFile, req);

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

                willThrow(new IllegalArgumentException("닉네임 중복"))
                        .given(duplicateNicknameUseCase).validateDuplicatedNickname("중복닉");

                // when
                IllegalArgumentException ex = catchThrowableOfType(
                        () -> service.modifyUserInfo(userId, null, req),
                        IllegalArgumentException.class
                );

                // then
                assertThat(ex).isNotNull();
                assertThat(ex.getMessage()).contains("닉네임 중복");
            }

            @Test
            @DisplayName("이미지 파일 검증 실패 시 예외 발생")
            void failInvalidImage() {
                // given
                Long userId = 1L;
                ModifyUserInfoRequest req = new ModifyUserInfoRequest(
                        "닉네임", 2L, null, List.of(20L), null,"소개"
                );

                willThrow(new IllegalArgumentException("잘못된 이미지"))
                        .given(profileImageFile).getOriginalFilename();

                // when
                IllegalArgumentException ex = catchThrowableOfType(
                        () -> service.modifyUserInfo(userId, profileImageFile, req),
                        IllegalArgumentException.class
                );

                // then
                assertThat(ex).isNotNull();
                assertThat(ex.getMessage()).contains("잘못된 이미지");
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
}
