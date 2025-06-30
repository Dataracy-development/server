package com.dataracy.modules.user.infra.mapper;

import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.infra.jpa.entity.UserEntity;
import com.dataracy.modules.user.infra.jpa.entity.UserTopicEntity;

import java.util.List;

public class UserMapper {

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
                userEntity.getAuthorLevel(),
                userEntity.getOccupation(),
                topicIds,
                userEntity.getVisitSource(),
                userEntity.isAdTermsAgreed(),
                userEntity.isDeleted()
        );
    }

    public static UserEntity toEntity(User user) {

        UserEntity userEntity =  UserEntity.toEntity(
                user.getId(),
                user.getProvider(),
                user.getProviderId(),
                user.getRole(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getAuthorLevel(),
                user.getOccupation(),
                user.getVisitSource(),
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
