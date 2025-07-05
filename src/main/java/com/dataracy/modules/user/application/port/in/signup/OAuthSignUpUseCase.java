package com.dataracy.modules.user.application.port.in.signup;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.user.application.dto.request.OnboardingRequest;

/**
 * 소셜 로그인 회원가입 유스케이스
 */
public interface OAuthSignUpUseCase {
    RefreshTokenResponse signUpOAuth(String registerToken, OnboardingRequest requestDto);
}
