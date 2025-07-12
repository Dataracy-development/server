package com.dataracy.modules.user.application.port.in.user;

import com.dataracy.modules.user.domain.model.vo.UserInfo;

/**
 * 이메일, 비밀번호가 일치하여 로그인이 가능한 상태인지 판단하는 유스케이스
 */
public interface IsLoginPossibleUseCase {
    UserInfo isLogin(String email, String password);
}
