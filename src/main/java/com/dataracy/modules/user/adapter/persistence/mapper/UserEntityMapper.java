package com.dataracy.modules.user.adapter.persistence.mapper;

import com.dataracy.modules.user.adapter.persistence.entity.UserEntity;
import com.dataracy.modules.user.adapter.persistence.entity.UserTopicEntity;
import com.dataracy.modules.user.domain.model.User;

import java.util.List;

/**
 * 유저 엔티티와 유저 도메인 모델을 변환하는 매퍼
 */
public class UserEntityMapper {
    // 유저 엔티티 -> 유저 도메인 모델
    public static User toDomain(UserEntity userEntity) {
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

    // 유저 도메인 모델 -> 유저 엔티티
    public static UserEntity toEntity(User user) {
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
