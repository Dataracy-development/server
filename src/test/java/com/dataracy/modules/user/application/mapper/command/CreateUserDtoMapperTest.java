/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.mapper.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

class CreateUserDtoMapperTest {

  private CreateUserDtoMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CreateUserDtoMapper();
    ReflectionTestUtils.setField(mapper, "defaultProfileImageUrl", "default.png");
  }

  @Test
  @DisplayName("SelfSignUpRequest → User 변환 시 필드 매핑 검증")
  void toDomainSelfSignUpRequestMapsFieldsCorrectly() {
    // given
    SelfSignUpRequest req =
        new SelfSignUpRequest(
            "test@email.com", "pw", "pw", "tester", 1L, 2L, List.of(10L, 20L), 3L, true);

    String providerId = "uuid-123";
    String encodedPassword = "encodedPw";

    // when
    User user = mapper.toDomain(req, providerId, encodedPassword);

    // then
    assertAll(
        () -> assertThat(user.getProvider()).isEqualTo(ProviderType.LOCAL),
        () -> assertThat(user.getRole()).isEqualTo(RoleType.ROLE_USER),
        () -> assertThat(user.getEmail()).isEqualTo("test@email.com"),
        () -> assertThat(user.getPassword()).isEqualTo("encodedPw"),
        () -> assertThat(user.getNickname()).isEqualTo("tester"),
        () -> assertThat(user.getAuthorLevelId()).isEqualTo(1L),
        () -> assertThat(user.getOccupationId()).isEqualTo(2L),
        () -> assertThat(user.getTopicIds()).containsExactly(10L, 20L),
        () -> assertThat(user.getVisitSourceId()).isEqualTo(3L),
        () -> assertThat(user.getProfileImageUrl()).isEqualTo("default.png"),
        () -> assertThat(user.getIntroductionText()).contains("tester"));
    assertAll(
        () -> assertThat(user.isAdTermsAgreed()).isTrue(),
        () -> assertThat(user.isDeleted()).isFalse());
  }

  @Test
  @DisplayName("OnboardingRequest → User 변환 시 필드 매핑 검증")
  void toDomainOnboardingRequestMapsFieldsCorrectly() {
    // given
    OnboardingRequest req = new OnboardingRequest("tester", 1L, 2L, List.of(10L, 20L), 3L, true);

    String provider = "GOOGLE";
    String providerId = "google-123";
    String email = "google@test.com";

    // when
    User user = mapper.toDomain(req, provider, providerId, email);

    // then
    assertAll(
        () -> assertThat(user.getProvider()).isEqualTo(ProviderType.GOOGLE),
        () -> assertThat(user.getRole()).isEqualTo(RoleType.ROLE_USER),
        () -> assertThat(user.getProviderId()).isEqualTo("google-123"),
        () -> assertThat(user.getEmail()).isEqualTo("google@test.com"),
        () -> assertThat(user.getNickname()).isEqualTo("tester"),
        () -> assertThat(user.getAuthorLevelId()).isEqualTo(1L),
        () -> assertThat(user.getOccupationId()).isEqualTo(2L),
        () -> assertThat(user.getTopicIds()).containsExactly(10L, 20L),
        () -> assertThat(user.getVisitSourceId()).isEqualTo(3L),
        () -> assertThat(user.getProfileImageUrl()).isEqualTo("default.png"),
        () -> assertThat(user.getIntroductionText()).contains("tester"),
        () -> assertThat(user.isAdTermsAgreed()).isTrue(),
        () -> assertThat(user.isDeleted()).isFalse());
  }

  @Test
  @DisplayName("SelfSignUpRequest: topicIds 비어있을 때 → 빈 리스트로 매핑")
  void toDomainSelfSignUpRequestWithEmptyTopicsReturnsEmptyList() {
    // given
    SelfSignUpRequest req =
        new SelfSignUpRequest(
            "empty@email.com", "pw", "pw", "emptyUser", 1L, 2L, Collections.emptyList(), 3L, false);

    // when
    User user = mapper.toDomain(req, "pid", "encodedPw");

    // then
    assertAll(
        () -> assertThat(user.getTopicIds()).isEmpty(),
        () -> assertThat(user.isAdTermsAgreed()).isFalse());
  }

  @Test
  @DisplayName("OnboardingRequest: provider 문자열이 잘못되면 IllegalArgumentException 발생")
  void toDomainOnboardingRequestWithInvalidProviderThrowsIllegalArgumentException() {
    // given
    OnboardingRequest req = new OnboardingRequest("tester", 1L, 2L, List.of(10L), 3L, false);

    // when & then
    UserException ex =
        catchThrowableOfType(
            () -> mapper.toDomain(req, "INVALID_PROVIDER", "id", "test@test.com"),
            UserException.class);
    assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.INVALID_PROVIDER_TYPE);
  }
}
