package com.dataracy.modules.project.domain.model.vo;


import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ProjectUserTest {

    private MockedStatic<LoggerFactory> loggerFactoryMock;
    private com.dataracy.modules.common.logging.DomainLogger domainLogger;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        domainLogger = org.mockito.Mockito.mock(com.dataracy.modules.common.logging.DomainLogger.class);
        given(LoggerFactory.domain()).willReturn(domainLogger);
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("ProjectUser 생성")
    class ProjectUserCreation {

        @Test
        @DisplayName("생성자로 ProjectUser 생성 성공")
        void createProjectUserWithConstructorSuccess() {
            // when
            ProjectUser projectUser = new ProjectUser(
                    1L,
                    RoleType.ROLE_USER,
                    "user@test.com",
                    "testUser",
                    "profile.jpg",
                    "안녕하세요",
                    1L,
                    2L
            );

            // then
            assertThat(projectUser.userId()).isEqualTo(1L);
            assertThat(projectUser.role()).isEqualTo(RoleType.ROLE_USER);
            assertThat(projectUser.email()).isEqualTo("user@test.com");
            assertThat(projectUser.nickname()).isEqualTo("testUser");
            assertThat(projectUser.profileImageUrl()).isEqualTo("profile.jpg");
            assertThat(projectUser.introductionText()).isEqualTo("안녕하세요");
            assertThat(projectUser.occupationId()).isEqualTo(1L);
            assertThat(projectUser.authorLevelId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("null 값들을 포함한 ProjectUser 생성 성공")
        void createProjectUserWithNullValuesSuccess() {
            // when
            ProjectUser projectUser = new ProjectUser(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            // then
            assertThat(projectUser.userId()).isNull();
            assertThat(projectUser.role()).isNull();
            assertThat(projectUser.email()).isNull();
            assertThat(projectUser.nickname()).isNull();
            assertThat(projectUser.profileImageUrl()).isNull();
            assertThat(projectUser.introductionText()).isNull();
            assertThat(projectUser.occupationId()).isNull();
            assertThat(projectUser.authorLevelId()).isNull();
        }

        @Test
        @DisplayName("ADMIN 역할을 가진 ProjectUser 생성 성공")
        void createProjectUserWithAdminRoleSuccess() {
            // when
            ProjectUser projectUser = new ProjectUser(
                    999L,
                    RoleType.ROLE_ADMIN,
                    "admin@test.com",
                    "admin",
                    "admin.jpg",
                    "관리자입니다",
                    3L,
                    4L
            );

            // then
            assertThat(projectUser.userId()).isEqualTo(999L);
            assertThat(projectUser.role()).isEqualTo(RoleType.ROLE_ADMIN);
            assertThat(projectUser.email()).isEqualTo("admin@test.com");
            assertThat(projectUser.nickname()).isEqualTo("admin");
            assertThat(projectUser.profileImageUrl()).isEqualTo("admin.jpg");
            assertThat(projectUser.introductionText()).isEqualTo("관리자입니다");
            assertThat(projectUser.occupationId()).isEqualTo(3L);
            assertThat(projectUser.authorLevelId()).isEqualTo(4L);
        }
    }

    @Nested
    @DisplayName("UserInfo에서 ProjectUser 변환")
    class FromUserInfoConversion {

        @Test
        @DisplayName("UserInfo에서 ProjectUser 변환 성공")
        void fromUserInfoSuccess() {
            // given
            UserInfo userInfo = new UserInfo(
                    1L,
                    RoleType.ROLE_USER,
                    "user@test.com",
                    "testUser",
                    2L,  // authorLevelId
                    1L,  // occupationId
                    List.of(1L, 2L),
                    3L,
                    "profile.jpg",
                    "안녕하세요"
            );

            // when
            ProjectUser projectUser = ProjectUser.fromUserInfo(userInfo);

            // then
            assertThat(projectUser.userId()).isEqualTo(1L);
            assertThat(projectUser.role()).isEqualTo(RoleType.ROLE_USER);
            assertThat(projectUser.email()).isEqualTo("user@test.com");
            assertThat(projectUser.nickname()).isEqualTo("testUser");
            assertThat(projectUser.profileImageUrl()).isEqualTo("profile.jpg");
            assertThat(projectUser.introductionText()).isEqualTo("안녕하세요");
            assertThat(projectUser.occupationId()).isEqualTo(1L);
            assertThat(projectUser.authorLevelId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("null 값들을 포함한 UserInfo에서 ProjectUser 변환 성공")
        void fromUserInfoWithNullValuesSuccess() {
            // given
            UserInfo userInfo = new UserInfo(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            // when
            ProjectUser projectUser = ProjectUser.fromUserInfo(userInfo);

            // then
            assertThat(projectUser.userId()).isNull();
            assertThat(projectUser.role()).isNull();
            assertThat(projectUser.email()).isNull();
            assertThat(projectUser.nickname()).isNull();
            assertThat(projectUser.profileImageUrl()).isNull();
            assertThat(projectUser.introductionText()).isNull();
            assertThat(projectUser.occupationId()).isNull();
            assertThat(projectUser.authorLevelId()).isNull();
        }

        @Test
        @DisplayName("null UserInfo로 변환 시 ProjectException 발생 및 로깅 검증")
        void fromUserInfoWithNullThrowsExceptionAndLogs() {
            // when & then
            ProjectException exception = catchThrowableOfType(() -> ProjectUser.fromUserInfo(null), ProjectException.class);
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.FAIL_GET_USER_INFO);

            // 로깅 검증
            then(domainLogger).should().logWarning("ProjectUser을 생성하기 위한 유저 정보가 주입되지 않았습니다.");
        }

        @Test
        @DisplayName("ADMIN 역할을 가진 UserInfo에서 ProjectUser 변환 성공")
        void fromUserInfoWithAdminRoleSuccess() {
            // given
            UserInfo userInfo = new UserInfo(
                    999L,
                    RoleType.ROLE_ADMIN,
                    "admin@test.com",
                    "admin",
                    4L,  // authorLevelId
                    3L,  // occupationId
                    List.of(5L, 6L),
                    7L,
                    "admin.jpg",
                    "관리자입니다"
            );

            // when
            ProjectUser projectUser = ProjectUser.fromUserInfo(userInfo);

            // then
            assertThat(projectUser.userId()).isEqualTo(999L);
            assertThat(projectUser.role()).isEqualTo(RoleType.ROLE_ADMIN);
            assertThat(projectUser.email()).isEqualTo("admin@test.com");
            assertThat(projectUser.nickname()).isEqualTo("admin");
            assertThat(projectUser.profileImageUrl()).isEqualTo("admin.jpg");
            assertThat(projectUser.introductionText()).isEqualTo("관리자입니다");
            assertThat(projectUser.occupationId()).isEqualTo(3L);
            assertThat(projectUser.authorLevelId()).isEqualTo(4L);
        }
    }

    @Nested
    @DisplayName("ProjectUser Record 특성")
    class ProjectUserRecordCharacteristics {

        @Test
        @DisplayName("동일한 값으로 생성된 ProjectUser는 equals로 같다고 판단")
        void projectUserEqualsTest() {
            // given
            ProjectUser projectUser1 = new ProjectUser(
                    1L, RoleType.ROLE_USER, "user@test.com", "testUser",
                    "profile.jpg", "안녕하세요", 1L, 2L
            );
            ProjectUser projectUser2 = new ProjectUser(
                    1L, RoleType.ROLE_USER, "user@test.com", "testUser",
                    "profile.jpg", "안녕하세요", 1L, 2L
            );

            // when & then
            assertThat(projectUser1).isEqualTo(projectUser2);
            assertThat(projectUser1.hashCode()).isEqualTo(projectUser2.hashCode());
        }

        @Test
        @DisplayName("다른 값으로 생성된 ProjectUser는 equals로 다르다고 판단")
        void projectUserNotEqualsTest() {
            // given
            ProjectUser projectUser1 = new ProjectUser(
                    1L, RoleType.ROLE_USER, "user@test.com", "testUser",
                    "profile.jpg", "안녕하세요", 1L, 2L
            );
            ProjectUser projectUser2 = new ProjectUser(
                    2L, RoleType.ROLE_USER, "user@test.com", "testUser",
                    "profile.jpg", "안녕하세요", 1L, 2L
            );

            // when & then
            assertThat(projectUser1).isNotEqualTo(projectUser2);
            assertThat(projectUser1.hashCode()).isNotEqualTo(projectUser2.hashCode());
        }

        @Test
        @DisplayName("toString 메서드가 올바른 문자열을 반환")
        void projectUserToStringTest() {
            // given
            ProjectUser projectUser = new ProjectUser(
                    1L, RoleType.ROLE_USER, "user@test.com", "testUser",
                    "profile.jpg", "안녕하세요", 1L, 2L
            );

            // when
            String toString = projectUser.toString();

            // then
            assertThat(toString).contains("ProjectUser[");
            assertThat(toString).contains("userId=1");
            assertThat(toString).contains("role=ROLE_USER");
            assertThat(toString).contains("email=user@test.com");
            assertThat(toString).contains("nickname=testUser");
        }
    }
}