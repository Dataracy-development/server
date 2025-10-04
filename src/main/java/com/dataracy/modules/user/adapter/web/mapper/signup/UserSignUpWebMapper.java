package com.dataracy.modules.user.adapter.web.mapper.signup;

import org.springframework.stereotype.Component;

import com.dataracy.modules.user.adapter.web.request.signup.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.signup.SelfSignUpWebRequest;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;

/** 유저 회원가입 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class UserSignUpWebMapper {
  /**
   * 자체 회원 가입 웹 요청 DTO를 애플리케이션 계층의 자체 회원 가입 요청 DTO로 변환합니다.
   *
   * @param webRequest 자체 회원 가입 웹 요청 데이터
   * @return 변환된 자체 회원 가입 애플리케이션 요청 DTO
   */
  public SelfSignUpRequest toApplicationDto(SelfSignUpWebRequest webRequest) {
    return new SelfSignUpRequest(
        webRequest.email(),
        webRequest.password(),
        webRequest.passwordConfirm(),
        webRequest.nickname(),
        webRequest.authorLevelId(),
        webRequest.occupationId(),
        webRequest.topicIds(),
        webRequest.visitSourceId(),
        webRequest.isAdTermsAgreed());
  }

  /**
   * 소셜 로그인 온보딩 웹 요청 DTO를 소셜 로그인 온보딩 애플리케이션 요청 DTO로 변환합니다.
   *
   * @param webRequest 소셜 로그인 온보딩 웹 요청 DTO
   * @return 변환된 소셜 로그인 온보딩 애플리케이션 요청 DTO
   */
  public OnboardingRequest toApplicationDto(OnboardingWebRequest webRequest) {
    return new OnboardingRequest(
        webRequest.nickname(),
        webRequest.authorLevelId(),
        webRequest.occupationId(),
        webRequest.topicIds(),
        webRequest.visitSourceId(),
        webRequest.isAdTermsAgreed());
  }
}
