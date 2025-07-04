package com.dataracy.modules.user.application.port.in;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;

/**
 * 소셜 로그인 시 신규 유저, 기존 유저 처리 유스케이스
 */
public interface HandleUserUseCase {
    RegisterTokenResponse handleNewUser(OAuthUserInfo oAuthUserInfo);
    RefreshTokenResponse handleExistingUser(OAuthUserInfo oAuthUserInfo);
}
