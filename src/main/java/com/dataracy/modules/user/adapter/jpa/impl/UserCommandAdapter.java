package com.dataracy.modules.user.adapter.jpa.impl;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.mapper.UserEntityMapper;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.application.port.out.jpa.UserCommandPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCommandAdapter implements UserCommandPort {
    private final UserJpaRepository userJpaRepository;

    /**
     * 주어진 유저 정보를 저장하고 저장된 유저 도메인 객체를 반환합니다.
     *
     * @param user 저장할 유저 도메인 객체
     * @return 저장된 유저 도메인 객체
     */
    @Override
    public User saveUser(User user) {
        UserEntity savedUser = userJpaRepository.save(UserEntityMapper.toEntity(user));
        LoggerFactory.db().logSave("UserEntity", String.valueOf(savedUser.getId()), "유저를 저장하였습니다.");
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
        LoggerFactory.db().logUpdate("UserEntity", String.valueOf(userEntity.getId()), "새 비밀번호를 변경하였습니다.");
    }

    /**
     * 지정된 사용자 ID에 해당하는 사용자를 탈퇴 처리합니다.
     *
     * @param userId 탈퇴할 사용자의 ID
     */
    public void withdrawalUser(Long userId) {
        userJpaRepository.withdrawalUser(userId);
        LoggerFactory.db().logUpdate("UserEntity", String.valueOf(userId), "해당 사용자를 탈퇴 처리한다.");
    }
}
