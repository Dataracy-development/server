package com.dataracy.modules.user.adapter.web.mapper;

import com.dataracy.modules.user.adapter.web.request.*;
import com.dataracy.modules.user.application.dto.request.*;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper {
    // 자체 회원 가입 웹 요청 DTO -> 자체 회원 가입 도메인 요청 DTO
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
                webRequest.isAdTermsAgreed()
        );
    }

    // 소셜 로그인 온보딩 웹 요청 DTO -> 소셜 로그인 온보딩 도메인 요청 DTO
    public OnboardingRequest toApplicationDto(OnboardingWebRequest webRequest) {
        return new OnboardingRequest(
                webRequest.nickname(),
                webRequest.authorLevelId(),
                webRequest.occupationId(),
                webRequest.topicIds(),
                webRequest.visitSourceId(),
                webRequest.isAdTermsAgreed()
        );
    }

    // 닉네임 중복 체크 웹 요청 DTO -> 닉네임 중복 체크 도메인 요청 DTO
    public DuplicateNicknameRequest toApplicationDto(DuplicateNicknameWebRequest webRequest) {
        return new DuplicateNicknameRequest(
                webRequest.nickname()
        );
    }

    // 비밀번호 변경 웹 요청 DTO -> 비밀번호 변경 도메인 요청 DTO
    public ChangePasswordRequest toApplicationDto(ChangePasswordWebRequest webRequest) {
        return new ChangePasswordRequest(
                webRequest.password(),
                webRequest.passwordConfirm()
        );
    }

    // 비밀번호 확인 웹 요청 DTO -> 비밀번호 확인 도메인 요청 DTO
    public ConfirmPasswordRequest toApplicationDto(ConfirmPasswordWebRequest webRequest) {
        return new ConfirmPasswordRequest (
                webRequest.password()
        );
    }
}
