package com.dataracy.modules.user.application.port.out.jpa;

/**
 * user db 존재 여부 포트
 */
public interface UserExistencePort {
    /**
 * 주어진 닉네임을 가진 사용자가 존재하는지 여부를 반환합니다.
 *
 * @param nickname 존재 여부를 확인할 닉네임
 * @return 사용자가 존재하면 true, 존재하지 않으면 false
 */
    boolean existsByNickname(String nickname);

    /**
 * 주어진 이메일 주소를 가진 사용자가 데이터베이스에 존재하는지 확인합니다.
 *
 * @param email 존재 여부를 확인할 이메일 주소
 * @return 사용자가 존재하면 true, 존재하지 않으면 false
 */
    boolean existsByEmail(String email);
}
