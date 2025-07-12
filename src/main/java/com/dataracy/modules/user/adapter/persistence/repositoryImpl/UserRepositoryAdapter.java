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

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository userJpaRepository;

    /**
     * 유저 아이디로 유저 조회
     * @param userId 유저 id
     * @return 유저
     */
    @Override
    public Optional<User> findUserById(Long userId) {
        return userJpaRepository.findById(userId)
                .map(UserEntityMapper::toDomain);
    }

    /**
     * 소셜 제공자 유저 id로 유저 저회
     * @param providerId 소셜 제공자 유저 id
     * @return 유저
     */
    @Override
    public Optional<User> findUserByProviderId(String providerId) {
        return userJpaRepository.findByProviderId(providerId)
                .map(UserEntityMapper::toDomain);
    }

    /**
     * 이메일로 유저 조회
     * @param email 이메일
     * @return 유저
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserEntityMapper::toDomain);
    }

    /**
     * 이메일로 유저 존재하는지 여부 확인
     * @param email 이메일
     * @return 유저
     */
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    /**
     * 닉네임으로 유저 존재하는지 여부 확인
     * @param nickname 닉네임
     * @return 유저
     */
    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    /**
     * 유저 저장
     * @param user 유저
     * @return 저장된 유저
     */
    @Override
    public User saveUser(User user) {
        UserEntity savedUser = userJpaRepository.save(UserEntityMapper.toEntity(user));
        return UserEntityMapper.toDomain(savedUser);
    }

    @Override
    public void changePassword(Long userId, String encodePassword) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.NOT_FOUND_USER));
        userEntity.changePassword(encodePassword);
    }

    /**
     * 유저 탈퇴
     * @param userId 유저 아이디
     */
    // @Override
    public void withdrawalUser(Long userId) {
        userJpaRepository.withdrawalUser(userId);
    }
}
