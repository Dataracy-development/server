package com.dataracy.modules.auth.application.port.in.auth;

import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;

public interface SelfLoginUseCase {
    /**
     * 자체 로그인
     *
     * @param requestDto 자체 로그인 도메인 요청 DTO
     * @return 리프레시 토큰
     */
    RefreshTokenResponse login(SelfLoginRequest requestDto);
}
