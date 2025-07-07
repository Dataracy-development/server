package com.dataracy.modules.auth.application.port.in.auth;

import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;

/**
 * 토큰 재발급 유스케이스
 */
public interface ReIssueTokenUseCase {
    ReIssueTokenResponse reIssueToken(String refreshToken);
}
