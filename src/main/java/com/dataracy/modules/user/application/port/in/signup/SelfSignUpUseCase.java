package com.dataracy.modules.user.application.port.in.signup;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;

public interface SelfSignUpUseCase {
    /**
     * 자체 회원가입
     *
     * @param requestDto 자체 회원가입을 위한 도메인 요청 DTO
     * @return 리프레시 토큰
     */
    RefreshTokenResponse signUpSelf(SelfSignUpRequest requestDto);
}
