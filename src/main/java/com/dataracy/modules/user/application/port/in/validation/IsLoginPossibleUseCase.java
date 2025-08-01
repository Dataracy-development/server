package com.dataracy.modules.user.application.port.in.validation;

import com.dataracy.modules.user.domain.model.vo.UserInfo;

public interface IsLoginPossibleUseCase {
    /**
 * 이메일과 비밀번호로 로그인 시도를 검증하고, 인증에 성공하면 해당 사용자의 정보를 반환합니다.
 *
 * @param email 로그인할 사용자의 이메일 주소
 * @param password 로그인할 사용자의 비밀번호
 * @return 인증에 성공한 사용자의 정보
 */
    UserInfo isLogin(String email, String password);
}
