package com.dataracy.modules.user.application.port.in.signup;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;

public interface OAuthSignUpUseCase {
    /**
     * 소셜 회원가입
     *
     * @param registerToken 레지스터 토큰
     * @param requestDto 추가 정보 입력을 위한 도메인 요청 DTO
     * @return 리프레시 토큰
     */
    RefreshTokenResponse signUpOAuth(String registerToken, OnboardingRequest requestDto);
}
