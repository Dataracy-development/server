package com.dataracy.modules.user.infra.jpa.repositoryImpl;

import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.repository.UserRepository;
import com.dataracy.modules.user.infra.jpa.entity.UserEntity;
import com.dataracy.modules.user.infra.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.infra.mapper.UserMapper;
import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User findUserById(Long userId) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.NOT_FOUND_USER));
        return UserMapper.toDomain(userEntity);
    }

    @Override
    public User findUserByProviderId(String providerId) {
        UserEntity userEntity = userJpaRepository.findByProviderId(providerId);
        if (userEntity == null) {
            return null;
        }
        return UserMapper.toDomain(userEntity);
    }

    @Override
    public User findUserByEmail(String email) {
        UserEntity userEntity = userJpaRepository.findByEmail(email);
        if (userEntity == null) {
            return null;
        }
        return UserMapper.toDomain(userEntity);
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
        UserEntity userEntity = userJpaRepository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(userEntity);
    }

    @Override
    public void markUserAsDeleted(Long userId) {
        userJpaRepository.markUserAsDeleted(userId);
    }
}
