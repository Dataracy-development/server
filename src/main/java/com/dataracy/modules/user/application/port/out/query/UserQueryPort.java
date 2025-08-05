package com.dataracy.modules.user.application.port.out.query;

import com.dataracy.modules.user.domain.model.User;

import java.util.Optional;

/**
 * user db 단건 조회 포트
 */
public interface UserQueryPort {
    /**
 * 주어진 사용자 ID로 사용자를 조회합니다.
 *
 * @param userId 조회할 사용자의 고유 식별자
 * @return 사용자가 존재하면 해당 User를 포함한 Optional, 존재하지 않으면 빈 Optional
 */
    Optional<User> findUserById(Long userId);

    /**
 * 외부 제공자 ID를 이용해 사용자를 조회합니다.
 *
 * @param providerId 외부 인증 제공자(예: 소셜 로그인)에서 발급한 고유 사용자 식별자
 * @return 해당 ID와 일치하는 사용자가 있으면 User를 포함한 Optional, 없으면 빈 Optional
 */
    Optional<User> findUserByProviderId(String providerId);

    /**
 * 이메일 주소로 사용자를 조회합니다.
 *
 * @param email 조회할 사용자의 이메일 주소
 * @return 해당 이메일을 가진 사용자가 존재하면 User를 포함한 Optional, 없으면 빈 Optional
 */
    Optional<User> findUserByEmail(String email);
}
