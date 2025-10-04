package com.dataracy.modules.user.application.port.in.query.auth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

public interface IsNewUserUseCase {
  /**
   * 소셜 로그인 시 제공된 OAuth 유저 정보로 사용자가 신규 유저인지 확인합니다.
   *
   * @param oAuthUserInfo 소셜 서버에서 받은 유저 정보 객체
   * @return 사용자가 신규 유저이면 true, 아니면 false
   */
  boolean isNewUser(OAuthUserInfo oAuthUserInfo);
}
