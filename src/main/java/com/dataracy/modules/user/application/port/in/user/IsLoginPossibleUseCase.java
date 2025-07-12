package com.dataracy.modules.user.application.port.in.user;

import com.dataracy.modules.user.domain.model.vo.UserInfo;

/**
 * 이메일, 비밀번호가 일치하여 로그인이 가능한 상태인지 판단하는 유스케이스
 */
public interface IsLoginPossibleUseCase {
    /**
 * 주어진 이메일과 비밀번호로 로그인 가능 여부를 확인하고, 일치하는 사용자의 정보를 반환합니다.
 *
 * @param email     로그인 시도할 사용자의 이메일 주소
 * @param password  로그인 시도할 사용자의 비밀번호
 * @return          인증에 성공한 사용자의 정보 객체
 */
UserInfo isLogin(String email, String password);
}
