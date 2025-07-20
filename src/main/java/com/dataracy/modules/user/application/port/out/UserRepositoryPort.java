package com.dataracy.modules.user.application.port.out;

import com.dataracy.modules.user.domain.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User db 접근 포트
 */
public interface UserRepositoryPort {
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
 * 주어진 사용자 엔터티를 저장하고, 저장된 사용자 엔터티를 반환합니다.
 *
 * @param user 저장할 사용자 엔터티
 * @return 저장된 사용자 엔터티
 */
    User saveUser(User user);
    /**
 * 지정한 닉네임을 가진 사용자가 존재하는지 확인합니다.
 *
 * @param nickname 존재 여부를 확인할 사용자 닉네임
 * @return 해당 닉네임을 가진 사용자가 있으면 true, 없으면 false
 */
    boolean existsByNickname(String nickname);
    /**
 * 지정한 이메일 주소를 가진 사용자가 데이터베이스에 존재하는지 여부를 반환합니다.
 *
 * @param email 존재 여부를 확인할 이메일 주소
 * @return 사용자가 존재하면 true, 존재하지 않으면 false
 */
    boolean existsByEmail(String email);
    /**
 * 이메일 주소를 기준으로 사용자를 조회합니다.
 *
 * @param email 조회할 사용자의 이메일 주소
 * @return 사용자가 존재하면 해당 User를 포함한 Optional, 존재하지 않으면 빈 Optional
 */
    Optional<User> findUserByEmail(String email);
    /**
 * 지정한 사용자 ID의 비밀번호를 주어진 인코딩된 새 비밀번호로 변경합니다.
 *
 * @param userId 비밀번호를 변경할 사용자 고유 식별자
 * @param encodePassword 변경할 인코딩된 새 비밀번호
 */
    void changePassword(Long userId, String encodePassword);

    /**
 * 주어진 사용자 ID 목록에 대해 각 사용자 ID와 해당 사용자의 닉네임을 매핑한 Map을 반환합니다.
 *
 * @param userIds 닉네임을 조회할 사용자 ID 목록
 * @return 각 사용자 ID와 해당 닉네임이 매핑된 Map
 */
Map<Long, String> findUsernamesByIds(List<Long> userIds);
}
