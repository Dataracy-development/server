package com.dataracy.modules.user.application.port.in.auth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RegisterTokenResponse;

public interface HandleUserUseCase {
    /**
 * 소셜 서버에서 받은 유저 정보를 기반으로 신규 사용자를 처리하고, 온보딩을 위한 레지스터 토큰을 반환합니다.
 *
 * @param oAuthUserInfo 소셜 서버로부터 제공된 신규 사용자 정보
 * @return 온보딩 절차에 사용되는 레지스터 토큰
 */
    RegisterTokenResponse handleNewUser(OAuthUserInfo oAuthUserInfo);

    /**
 * 기존 소셜 계정 유저의 인증 처리를 수행합니다.
 *
 * @param oAuthUserInfo 소셜 서버에서 받은 기존 유저의 정보
 * @return 인증된 유저에게 발급되는 리프레시 토큰 응답
 */
    RefreshTokenResponse handleExistingUser(OAuthUserInfo oAuthUserInfo);
}
