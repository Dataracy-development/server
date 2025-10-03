/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.mapper.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;

/** 도메인 요청 DTO -> 도메인 모델 */
@Component
public class CreateUserDtoMapper {
  @Value("${default.profile.image-url}")
  private String defaultProfileImageUrl;

  /**
   * SelfSignUpRequest DTO를 기반으로 로컬 ProviderType의 User 도메인 객체를 생성합니다.
   *
   * @param requestDto 회원가입 요청 정보를 담은 DTO
   * @param providerId 인증 제공자 식별자
   * @param encodedPassword 암호화된 비밀번호
   * @return 생성된 User 도메인 객체
   */
  public User toDomain(SelfSignUpRequest requestDto, String providerId, String encodedPassword) {
    String introductionText = "안녕하세요. " + requestDto.nickname() + "입니다.";
    return User.of(
        null,
        ProviderType.LOCAL,
        providerId,
        RoleType.ROLE_USER,
        requestDto.email(),
        encodedPassword,
        requestDto.nickname(),
        requestDto.authorLevelId(),
        requestDto.occupationId(),
        requestDto.topicIds(),
        requestDto.visitSourceId(),
        defaultProfileImageUrl,
        introductionText,
        requestDto.isAdTermsAgreed(),
        false);
  }

  /**
   * OnboardingRequest DTO를 기반으로 새로운 User 도메인 객체를 생성합니다.
   *
   * @param requestDto 온보딩 요청 정보를 담은 DTO
   * @param provider 소셜 로그인 제공자 식별자
   * @param providerId 소셜 제공자별 사용자 고유 ID
   * @param email 사용자 이메일 주소
   * @return 생성된 User 도메인 객체
   */
  public User toDomain(
      OnboardingRequest requestDto, String provider, String providerId, String email) {
    String introductionText = "안녕하세요. " + requestDto.nickname() + "입니다.";
    return User.of(
        null,
        ProviderType.of(provider),
        providerId,
        RoleType.ROLE_USER,
        email,
        null,
        requestDto.nickname(),
        requestDto.authorLevelId(),
        requestDto.occupationId(),
        requestDto.topicIds(),
        requestDto.visitSourceId(),
        defaultProfileImageUrl,
        introductionText,
        requestDto.isAdTermsAgreed(),
        false);
  }
}
