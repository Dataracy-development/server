package com.dataracy.modules.user.application.port.out.jpa;

import com.dataracy.modules.user.domain.model.User;

import java.util.Optional;

/**
 * user db 단건 조회 포트
 */
public interface UserQueryPort {
    /**
     * 사용자 ID로 사용자를 조회합니다.
     *
     * @param userId 조회할 사용자의 고유 ID
     * @return 사용자가 존재하면 User를 포함한 Optional, 없으면 빈 Optional
     */
    Optional<User> findUserById(Long userId);

    /**
     * 외부 제공자 ID로 사용자를 조회합니다.
     *
     * @param providerId 외부 제공자(예: 소셜 로그인)에서 발급한 사용자 식별자
     * @return 사용자가 존재하면 해당 User를 포함한 Optional, 없으면 빈 Optional
     */
    Optional<User> findUserByProviderId(String providerId);

    /**
     * 이메일 주소를 기준으로 사용자를 조회합니다.
     *
     * @param email 조회할 사용자의 이메일 주소
     * @return 사용자가 존재하면 해당 User를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    Optional<User> findUserByEmail(String email);
}
