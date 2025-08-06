package com.dataracy.modules.user.application.port.in.query.auth;

import com.dataracy.modules.user.domain.model.vo.UserInfo;

public interface IsLoginPossibleUseCase {
    /**
 * 이메일과 비밀번호로 사용자를 인증하고, 인증에 성공하면 해당 사용자의 정보를 반환합니다.
 *
 * @param email 인증할 사용자의 이메일 주소
 * @param password 인증할 사용자의 비밀번호
 * @return 인증에 성공한 사용자의 정보
 */
    UserInfo loginAndGetUserInfo(String email, String password);
}
