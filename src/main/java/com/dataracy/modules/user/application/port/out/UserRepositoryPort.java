package com.dataracy.modules.user.application.port.out;

import com.dataracy.modules.user.domain.model.User;

import java.util.Optional;

/**
 * User db 접근 포트
 */
public interface UserRepositoryPort {
    /**
     * 주어진 사용자 ID로 사용자를 조회합니다.
     *
     * @param userId 조회할 사용자의 고유 식별자
     * @return 사용자가 존재하면 해당 User를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    Optional<User> findUserById(Long userId);
    /**
     * 주어진 외부 제공자 ID로 사용자를 조회합니다.
     *
     * @param providerId 외부 제공자(예: 소셜 로그인)에서 발급한 사용자 식별자
     * @return 사용자가 존재하면 해당 User를 포함한 Optional, 없으면 빈 Optional
     */
    Optional<User> findUserByProviderId(String providerId);
    /**
     * 사용자 엔터티를 데이터베이스에 저장하고 저장된 사용자 정보를 반환합니다.
     *
     * @param user 저장할 사용자 엔터티
     * @return 저장된 사용자 엔터티
     */
    User saveUser(User user);
    /**
     * 주어진 닉네임을 가진 사용자가 존재하는지 여부를 반환합니다.
     *
     * @param nickname 확인할 사용자 닉네임
     * @return 닉네임을 가진 사용자가 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByNickname(String nickname);
    /**
     * 주어진 이메일을 가진 사용자가 존재하는지 확인합니다.
     *
     * @param email 확인할 사용자의 이메일 주소
     * @return 해당 이메일을 가진 사용자가 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByEmail(String email);
    /**
     * 주어진 이메일 주소로 사용자를 조회합니다.
     *
     * @param email 조회할 사용자의 이메일 주소
     * @return 사용자가 존재하면 해당 User를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    Optional<User> findUserByEmail(String email);
    /**
     * 지정된 사용자 ID에 해당하는 사용자의 비밀번호를 주어진 인코딩된 비밀번호로 변경합니다.
     *
     * @param userId 비밀번호를 변경할 사용자의 고유 ID
     * @param encodePassword 새로 설정할 인코딩된 비밀번호
     */
    void changePassword(Long userId, String encodePassword);
}
