package com.dataracy.modules.user.adapter.persistence.repositoryImpl;

import com.dataracy.modules.user.adapter.persistence.entity.UserEntity;
import com.dataracy.modules.user.adapter.persistence.mapper.UserEntityMapper;
import com.dataracy.modules.user.adapter.persistence.repository.UserJpaRepository;
import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository userJpaRepository;

//    @Override
    public User findUserById(Long userId) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.NOT_FOUND_USER));
        return UserEntityMapper.toDomain(userEntity);
    }

    @Override
    public User findUserByProviderId(String providerId) {
        UserEntity userEntity = userJpaRepository.findByProviderId(providerId);
        if (userEntity == null) {
            return null;
        }
        return UserEntityMapper.toDomain(userEntity);
    }

    @Override
    public User findUserByEmail(String email) {
        UserEntity userEntity = userJpaRepository.findByEmail(email);
        if (userEntity == null) {
            return null;
        }
        return UserEntityMapper.toDomain(userEntity);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public User saveUser(User user) {
        UserEntity savedUser = userJpaRepository.save(UserEntityMapper.toEntity(user));
        return UserEntityMapper.toDomain(savedUser);
    }

//    @Override
    public void withdrawalUser(Long userId) {
        userJpaRepository.withdrawalUser(userId);
    }
}
