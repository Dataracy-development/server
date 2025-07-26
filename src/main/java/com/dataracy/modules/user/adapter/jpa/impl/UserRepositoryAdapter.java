package com.dataracy.modules.user.adapter.jpa.impl;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.mapper.UserEntityMapper;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository userJpaRepository;

    /**
     * 주어진 사용자 ID로 사용자를 조회하여 Optional로 반환합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자가 존재하면 해당 User 객체를, 없으면 빈 Optional을 반환합니다.
     */
    @Override
    public Optional<User> findUserById(Long userId) {
        return userJpaRepository.findById(userId)
                .map(UserEntityMapper::toDomain);
    }

    /**
     * 소셜 제공자 ID로 사용자를 조회하여 Optional로 반환합니다.
     *
     * @param providerId 소셜 제공자에서 발급한 사용자 ID
     * @return 해당 providerId에 해당하는 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()
     */
    @Override
    public Optional<User> findUserByProviderId(String providerId) {
        return userJpaRepository.findByProviderId(providerId)
                .map(UserEntityMapper::toDomain);
    }

    /**
     * 이메일을 기준으로 사용자를 조회하여 Optional로 반환합니다.
     *
     * @param email 조회할 사용자의 이메일 주소
     * @return 해당 이메일을 가진 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserEntityMapper::toDomain);
    }

    /**
     * 주어진 이메일을 가진 사용자가 존재하는지 여부를 반환합니다.
     *
     * @param email 확인할 이메일 주소
     * @return 사용자가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    /**
     * 주어진 닉네임을 가진 사용자가 존재하는지 여부를 반환합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 사용자가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    /**
     * 주어진 유저 정보를 저장하고 저장된 유저 도메인 객체를 반환합니다.
     *
     * @param user 저장할 유저 도메인 객체
     * @return 저장된 유저 도메인 객체
     */
    @Override
    public User saveUser(User user) {
        UserEntity savedUser = userJpaRepository.save(UserEntityMapper.toEntity(user));
        return UserEntityMapper.toDomain(savedUser);
    }

    /**
     * 사용자의 비밀번호를 새로운 인코딩된 값으로 변경합니다.
     *
     * @param userId 비밀번호를 변경할 사용자의 ID
     * @param encodePassword 새로 설정할 인코딩된 비밀번호
     * @throws UserException 사용자를 찾을 수 없는 경우 발생합니다.
     */
    @Override
    public void changePassword(Long userId, String encodePassword) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorStatus.NOT_FOUND_USER));
        userEntity.changePassword(encodePassword);
    }

    /**
     * 주어진 사용자 ID 목록에 해당하는 사용자들의 닉네임을 사용자 ID별로 매핑하여 반환합니다.
     *
     * @param userIds 조회할 사용자 ID 목록
     * @return 각 사용자 ID와 해당 닉네임이 매핑된 맵
     */
    @Override
    public Map<Long, String> findUsernamesByIds(List<Long> userIds) {
        return userJpaRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getNickname));
    }

    /**
     * 주어진 사용자 ID 목록에 대해 각 사용자의 프로필 이미지 URL을 반환합니다.
     *
     * @param userIds 프로필 이미지 URL을 조회할 사용자 ID 목록
     * @return 사용자 ID를 키로 하고 프로필 이미지 URL(없을 경우 빈 문자열)을 값으로 하는 맵
     */
    @Override
    public Map<Long, String> findUserThumbnailsByIds(List<Long> userIds) {
        return userJpaRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        user -> Optional.ofNullable(user.getProfileImageUrl()).orElse("")
                ));
    }

    @Override
    public Map<Long, String> findUserAuthorLevelIds(List<Long> userIds) {
        return userJpaRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        user -> String.valueOf(Optional.ofNullable(user.getAuthorLevelId()).orElse(1L))
                ));
    }

    /**
     * 지정된 사용자 ID에 해당하는 사용자를 탈퇴 처리합니다.
     *
     * @param userId 탈퇴할 사용자의 ID
     */
    public void withdrawalUser(Long userId) {
        userJpaRepository.withdrawalUser(userId);
    }
}
