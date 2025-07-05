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

    /**
     * 유저 아이디로 유저 조회
     * @param userId 유저 id
     * @return 유저
     */
//    @Override
    public User findUserById(Long userId) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.NOT_FOUND_USER));
        return UserEntityMapper.toDomain(userEntity);
    }

    /**
     * 소셜 제공자 유저 id로 유저 저회
     * @param providerId 소셜 제공자 유저 id
     * @return 유저
     */
    @Override
    public User findUserByProviderId(String providerId) {
        UserEntity userEntity = userJpaRepository.findByProviderId(providerId);
        if (userEntity == null) {
            return null;
        }
        return UserEntityMapper.toDomain(userEntity);
    }

    /**
     * 이메일로 유저 조회
     * @param email 이메일
     * @return 유저
     */
    @Override
    public User findUserByEmail(String email) {
        UserEntity userEntity = userJpaRepository.findByEmail(email);
        if (userEntity == null) {
            return null;
        }
        return UserEntityMapper.toDomain(userEntity);
    }

    /**
     * 이메일로 유저 존재하는지 여부 확인
     * @param email 이메일
     * @return 유저
     */
    @Override
    public Boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    /**
     * 닉네임으로 유저 존재하는지 여부 확인
     * @param nickname 닉네임
     * @return 유저
     */
    @Override
    public Boolean existsByNickname(String nickname) {
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

    /**
     * 유저 탈퇴
     * @param userId 유저 아이디
     */
//    @Override
    public void withdrawalUser(Long userId) {
        userJpaRepository.withdrawalUser(userId);
    }
}
