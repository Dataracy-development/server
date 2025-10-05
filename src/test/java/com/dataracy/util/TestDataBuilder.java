package com.dataracy.util;

import java.util.List;

import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

/** 테스트용 데이터 빌더 유틸리티 */
public final class TestDataBuilder {

  private TestDataBuilder() {
    // Utility class
  }

  /** 기본 사용자 정보 생성 */
  public static UserInfo createDefaultUserInfo() {
    return new UserInfo(
        1L,
        RoleType.ROLE_USER,
        "test@example.com",
        "TestUser",
        1L,
        2L,
        List.of(1L, 2L),
        3L,
        "http://example.com/profile.jpg",
        "Hello, I am a test user.");
  }

  /** 기본 사용자 엔티티 생성 */
  public static User createDefaultUser() {
    return User.builder()
        .id(1L)
        .provider(ProviderType.LOCAL)
        .providerId("test-provider-id")
        .role(RoleType.ROLE_USER)
        .email("test@example.com")
        .password("encoded-password")
        .nickname("TestUser")
        .authorLevelId(1L)
        .occupationId(2L)
        .topicIds(List.of(1L, 2L))
        .visitSourceId(3L)
        .profileImageUrl("http://example.com/profile.jpg")
        .introductionText("Hello, I am a test user.")
        .isAdTermsAgreed(true)
        .isDeleted(false)
        .build();
  }

  /** 관리자 사용자 정보 생성 */
  public static UserInfo createAdminUserInfo() {
    return new UserInfo(
        2L,
        RoleType.ROLE_ADMIN,
        "admin@example.com",
        "AdminUser",
        1L,
        2L,
        List.of(1L, 2L),
        3L,
        "http://example.com/admin-profile.jpg",
        "I am an admin user.");
  }

  /** 구글 사용자 정보 생성 */
  public static UserInfo createGoogleUserInfo() {
    return new UserInfo(
        3L,
        RoleType.ROLE_USER,
        "google@example.com",
        "GoogleUser",
        1L,
        2L,
        List.of(1L, 2L),
        3L,
        "http://example.com/google-profile.jpg",
        "I am a Google user.");
  }
}
