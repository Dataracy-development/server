package com.dataracy.modules.user.application.port.out;

import com.dataracy.modules.user.domain.model.User;

import java.util.Optional;

/**
 * User db 접근 포트
 */
public interface UserRepositoryPort {
    Optional<User> findUserById(Long userId);
    Optional<User> findUserByProviderId(String providerId);
    User saveUser(User user);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<User> findUserByEmail(String email);

    void changePassword(Long userId, String encodePassword);
}
