package com.dataracy.modules.user.application.port.in.signup;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;

public interface OAuthSignUpUseCase {
    /**
 * 소셜(OAuth) 회원가입을 처리하고 리프레시 토큰을 반환합니다.
 *
 * @param registerToken 소셜 회원가입을 위한 등록 토큰
 * @param requestDto 온보딩에 필요한 추가 사용자 정보 요청 객체
 * @return 발급된 리프레시 토큰 응답 객체
 */
    RefreshTokenResponse signUpOAuth(String registerToken, OnboardingRequest requestDto);
}
