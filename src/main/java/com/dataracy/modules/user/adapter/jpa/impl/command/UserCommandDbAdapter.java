package com.dataracy.modules.user.adapter.jpa.impl.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.entity.UserTopicEntity;
import com.dataracy.modules.user.adapter.jpa.mapper.UserEntityMapper;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.adapter.jpa.repository.UserTopicJpaRepository;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCommandDbAdapter implements UserCommandPort {
    private final UserJpaRepository userJpaRepository;
    private final UserTopicJpaRepository userTopicJpaRepository;

    /**
     * 유저 도메인 객체를 저장한 후, 저장된 결과를 반환합니다.
     *
     * @param user 저장할 유저 도메인 객체
     * @return 저장된 유저 도메인 객체
     */
    @Override
    public User saveUser(User user) {
        UserEntity savedUser = userJpaRepository.save(UserEntityMapper.toEntity(user));
        LoggerFactory.db().logSave("UserEntity", String.valueOf(savedUser.getId()), "DB에 유저를 저장하였습니다.");
        return UserEntityMapper.toDomain(savedUser);
    }

    /**
     * 지정한 사용자의 비밀번호를 새로운 인코딩된 값으로 변경합니다.
     *
     * @param userId 비밀번호를 변경할 대상 사용자의 ID
     * @param encodePassword 새로 설정할 인코딩된 비밀번호
     * @throws UserException 사용자를 찾을 수 없는 경우 발생합니다.
     */
    @Override
    public void changePassword(Long userId, String encodePassword) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("UserEntity", "[비밀번호 변경] 사용자를 찾을 수 없습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
        userEntity.changePassword(encodePassword);
        LoggerFactory.db().logUpdate("UserEntity", String.valueOf(userEntity.getId()), "새 비밀번호를 변경하였습니다.");
    }

    @Override
    public void modifyUserInfo(Long userId, ModifyUserInfoRequest requestDto) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("UserEntity", "[회원 정보 수정] 사용자를 찾을 수 없습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
        userEntity.modifyUserInfo(requestDto);

        // 기존 관심 주제 전부 삭제 (쿼리 직접)
        userTopicJpaRepository.deleteAllByUserId(userId);

        // 새 관심 주제 추가
        for (Long topicId : requestDto.topicIds()) {
            UserTopicEntity topicEntity = UserTopicEntity.of(userEntity, topicId);
            userEntity.addUserTopic(topicEntity);
            userTopicJpaRepository.save(topicEntity);
        }
        userJpaRepository.save(userEntity);
    }

    @Override
    public void updateProfileImageFile(Long userId, String profileImageFileUrl) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("UserEntity", "[회원 정보 수정] 사용자를 찾을 수 없습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
        userEntity.modifyProfileImageUrl(profileImageFileUrl);
        userJpaRepository.save(userEntity);
    }

    /**
     * 지정된 사용자 ID에 해당하는 사용자를 탈퇴 상태로 변경합니다.
     *
     * @param userId 탈퇴 처리할 사용자의 고유 ID
     */
    @Override
    public void withdrawalUser(Long userId) {
        userJpaRepository.withdrawalUser(userId);
        LoggerFactory.db().logUpdate("UserEntity", String.valueOf(userId), "해당 사용자를 탈퇴 처리한다.");
    }
}
