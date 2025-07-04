package com.dataracy.modules.user.application.port.in;

import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.user.application.dto.request.SelfSignUpRequest;

/**
 * 자체 회원가입 유스케이스
 */
public interface SelfSignUpUseCase {
    RefreshTokenResponse signUpSelf(SelfSignUpRequest requestDto);
}
