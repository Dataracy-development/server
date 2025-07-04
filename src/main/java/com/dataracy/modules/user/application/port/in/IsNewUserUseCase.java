package com.dataracy.modules.user.application.port.in;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

/**
 * 소셜로그인 시 기존 유저인지 여부를 판단하는 유스케이스
 */
public interface IsNewUserUseCase {
    boolean isNewUser(OAuthUserInfo oAuthUserInfo);
}
