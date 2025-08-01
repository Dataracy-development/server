package com.dataracy.modules.user.application.port.in.auth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

public interface IsNewUserUseCase {
    /**
     * 소셜 로그인 시 신규 유저인지 여부를 확인
     *
     * @param oAuthUserInfo 소셜 서버로부터 제공받은 유저 정보
     * @return 신규 유저 여부
     */
    boolean isNewUser(OAuthUserInfo oAuthUserInfo);
}
