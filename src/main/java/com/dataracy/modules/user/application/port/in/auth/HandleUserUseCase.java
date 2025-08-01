package com.dataracy.modules.user.application.port.in.auth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;

public interface HandleUserUseCase {
    /**
     * 신규 유저 핸들링
     *
     * @param oAuthUserInfo 소셜 서버로부터 제공받은 유저 정보
     * @return 온보딩을 위한 레지스터 토큰
     */
    RegisterTokenResponse handleNewUser(OAuthUserInfo oAuthUserInfo);

    /**
     * 기존 유저 핸들링
     *
     * @param oAuthUserInfo 소셜 서버로부터 제공받은 유저 정보
     * @return 리프레시 토큰
     */
    RefreshTokenResponse handleExistingUser(OAuthUserInfo oAuthUserInfo);
}
