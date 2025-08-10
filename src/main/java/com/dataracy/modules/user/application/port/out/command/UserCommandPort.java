package com.dataracy.modules.user.application.port.out.command;

import com.dataracy.modules.user.domain.model.User;

public interface UserCommandPort {
    /**
     * 사용자를 데이터베이스에 저장하고, 저장된 사용자 도메인 모델을 반환합니다.
     *
     * @param user 저장 또는 갱신할 사용자 도메인 모델
     * @return 저장된 사용자 도메인 모델
     */
    User saveUser(User user);

    /**
     * 지정한 사용자 ID에 해당하는 사용자의 비밀번호를 새로운 인코딩된 비밀번호로 업데이트합니다.
     *
     * @param userId 비밀번호를 변경할 대상 사용자의 고유 식별자
     * @param encodePassword 새로 설정할 인코딩된 비밀번호
     */
    void changePassword(Long userId, String encodePassword);
}
