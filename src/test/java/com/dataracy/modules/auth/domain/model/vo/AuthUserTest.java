package com.dataracy.modules.auth.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AuthUser 테스트")
class AuthUserTest {

    @Test
    @DisplayName("AuthUser 생성자 테스트")
    void authUser_ShouldCreateCorrectly() {
        // Given
        Long userId = 1L;
        RoleType role = RoleType.ROLE_USER;
        String email = "test@example.com";
        String nickname = "testUser";

        // When
        AuthUser authUser = new AuthUser(userId, role, email, nickname);

        // Then
        assertThat(authUser.userId()).isEqualTo(userId);
        assertThat(authUser.role()).isEqualTo(role);
        assertThat(authUser.email()).isEqualTo(email);
        assertThat(authUser.nickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("AuthUser.from - UserInfo로부터 AuthUser 생성")
    void from_ShouldCreateAuthUserFromUserInfo() {
        // Given
        UserInfo userInfo = new UserInfo(
                123L,
                RoleType.ROLE_ADMIN,
                "admin@example.com",
                "adminUser",
                null,
                null,
                null,
                null,
                "admin@example.com",
                "Admin User"
        );

        // When
        AuthUser authUser = AuthUser.from(userInfo);

        // Then
        assertThat(authUser.userId()).isEqualTo(123L);
        assertThat(authUser.role()).isEqualTo(RoleType.ROLE_ADMIN);
        assertThat(authUser.email()).isEqualTo("admin@example.com");
        assertThat(authUser.nickname()).isEqualTo("adminUser");
    }

    @Test
    @DisplayName("AuthUser.from - null UserInfo 처리")
    void from_ShouldHandleNullUserInfo() {
        // Given
        UserInfo userInfo = null;

        // When & Then
        assertThatThrownBy(() -> AuthUser.from(userInfo))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("AuthUser - equals 및 hashCode 테스트")
    void authUser_ShouldHaveCorrectEqualsAndHashCode() {
        // Given
        AuthUser authUser1 = new AuthUser(1L, RoleType.ROLE_USER, "test@example.com", "testUser");
        AuthUser authUser2 = new AuthUser(1L, RoleType.ROLE_USER, "test@example.com", "testUser");
        AuthUser authUser3 = new AuthUser(2L, RoleType.ROLE_USER, "test@example.com", "testUser");

        // When & Then
        assertThat(authUser1).isEqualTo(authUser2);
        assertThat(authUser1.hashCode()).isEqualTo(authUser2.hashCode());
        assertThat(authUser1).isNotEqualTo(authUser3);
    }

    @Test
    @DisplayName("AuthUser - toString 테스트")
    void authUser_ShouldHaveCorrectToString() {
        // Given
        AuthUser authUser = new AuthUser(1L, RoleType.ROLE_USER, "test@example.com", "testUser");

        // When
        String toString = authUser.toString();

        // Then
        assertThat(toString).contains("AuthUser");
        assertThat(toString).contains("1");
        assertThat(toString).contains("ROLE_USER");
        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("testUser");
    }

    @Test
    @DisplayName("AuthUser - 다양한 RoleType 테스트")
    void authUser_ShouldHandleDifferentRoleTypes() {
        // Given & When & Then
        AuthUser user = new AuthUser(1L, RoleType.ROLE_USER, "user@example.com", "user");
        AuthUser admin = new AuthUser(2L, RoleType.ROLE_ADMIN, "admin@example.com", "admin");
        AuthUser anonymous = new AuthUser(null, RoleType.ROLE_ANONYMOUS, null, "anonymous");

        assertThat(user.role()).isEqualTo(RoleType.ROLE_USER);
        assertThat(admin.role()).isEqualTo(RoleType.ROLE_ADMIN);
        assertThat(anonymous.role()).isEqualTo(RoleType.ROLE_ANONYMOUS);
    }
}
