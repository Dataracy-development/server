package com.dataracy.modules.user.adapter.persistence.mapper;

import com.dataracy.modules.user.adapter.persistence.entity.UserEntity;
import com.dataracy.modules.user.adapter.persistence.entity.UserTopicEntity;
import com.dataracy.modules.user.domain.model.User;

import java.util.List;

/**
 * 유저 엔티티와 유저 도메인 모델을 변환하는 매퍼
 */
public class UserEntityMapper {
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
     * 유저 도메인 모델을 유저 엔티티로 변환합니다.
     *
     * 입력된 User 객체의 정보를 기반으로 UserEntity를 생성하고, 관련 토픽 ID 목록을 UserTopicEntity로 변환하여 연결합니다.
     *
     * @param user 변환할 유저 도메인 모델
     * @return 변환된 UserEntity 객체, 입력이 null인 경우 null 반환
     */
    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity userEntity =  UserEntity.toEntity(
                user.getId(),
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
        List<UserTopicEntity> userTopicEntities = user.getTopicIds().stream()
                .map(topicId -> UserTopicEntity.of(userEntity, topicId))
                .toList();

        userTopicEntities.forEach(userEntity::addUserTopic);

        return userEntity;
    }
}
