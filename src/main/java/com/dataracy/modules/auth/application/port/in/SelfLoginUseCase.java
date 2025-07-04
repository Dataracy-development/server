package com.dataracy.modules.auth.application.port.in;

import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;

/**
 * 자체 로그인 유스케이스
 */
public interface SelfLoginUseCase {
    RefreshTokenResponse login(SelfLoginRequest requestDto);
}
