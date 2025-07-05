package com.dataracy.modules.user.application.port.out;

import com.dataracy.modules.user.domain.model.User;

/**
 * User db 접근 포트
 */
public interface UserRepositoryPort {
    User findUserById(Long userId);
    User findUserByProviderId(String providerId);
    User saveUser(User user);
    Boolean existsByNickname(String nickname);
    Boolean existsByEmail(String email);
    User findUserByEmail(String email);

    void changePassword(Long userId, String encodePassword);
}
