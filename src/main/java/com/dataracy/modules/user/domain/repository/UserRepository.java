package com.dataracy.modules.user.domain.repository;

import com.dataracy.modules.user.domain.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User findUserById(Long id);
    User findUserByProviderId(String providerId);
    User findUserByEmail(String email);
    Boolean existsByNickname(String nickname);

    Boolean existsByEmail(String email);
    User saveUser(User user);
    void markUserAsDeleted(Long userId);
}
