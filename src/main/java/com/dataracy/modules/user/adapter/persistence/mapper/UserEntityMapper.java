package com.dataracy.modules.user.adapter.persistence.mapper;

import com.dataracy.modules.user.adapter.persistence.entity.UserEntity;
import com.dataracy.modules.user.adapter.persistence.entity.UserTopicEntity;
import com.dataracy.modules.user.domain.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 유저 엔티티와 유저 도메인 모델을 변환하는 매퍼
 */
public final class UserEntityMapper {
    private UserEntityMapper() {
    }

    /**
     * UserEntity 객체를 User 도메인 모델로 변환합니다.
     *
     * @param userEntity 변환할 UserEntity 객체
     * @return 변환된 User 도메인 모델 객체, 입력이 null이면 null 반환
     */
    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        List<Long> topicIds = userEntity.getUserTopicEntities().stream()
                .map(UserTopicEntity::getTopicId)
                .toList();

        return User.toDomain(
                userEntity.getId(),
                userEntity.getProvider(),
                userEntity.getProviderId(),
                userEntity.getRole(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getNickname(),
                userEntity.getAuthorLevelId(),
                userEntity.getOccupationId(),
                topicIds,
                userEntity.getVisitSourceId(),
                userEntity.isAdTermsAgreed(),
                userEntity.isDeleted()
        );
    }

    /**
     * 유저 도메인 모델 객체를 UserEntity로 변환합니다.
     *
     * User 객체의 필드와 토픽 ID 목록을 기반으로 UserEntity와 연관된 UserTopicEntity 목록을 생성하여 연결합니다.
     *
     * @param user 변환할 유저 도메인 모델 객체
     * @return 변환된 UserEntity 객체, 입력이 null이면 null을 반환합니다.
     */
    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity userEntity =  UserEntity.toEntity(
                user.getProvider(),
                user.getProviderId(),
                user.getRole(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getAuthorLevelId(),
                user.getOccupationId(),
                user.getVisitSourceId(),
                user.isAdTermsAgreed(),
                user.isDeleted()
        );

        // topicIds → userTopicEntities 변환 후 연결
        List<UserTopicEntity> userTopicEntities = Optional.ofNullable(user.getTopicIds())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(topicId -> UserTopicEntity.of(userEntity, topicId))
                .toList();
        userTopicEntities.forEach(userEntity::addUserTopic);

        return userEntity;
    }
}
