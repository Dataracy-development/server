package com.dataracy.modules.auth.application.port.in.auth;

import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;

public interface ReIssueTokenUseCase {
    /**
     * 리프레시 토큰을 통해 어세스 토큰과 리프레시 토큰을 재발급한다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 어세스토큰, 리프레시 토큰, 유효 기간
     */
    ReIssueTokenResponse reIssueToken(String refreshToken);
}
