package com.dataracy.modules.user.adapter.jpa.mapper;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.entity.UserTopicEntity;
import com.dataracy.modules.user.domain.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 유저 엔티티와 유저 도메인 모델을 변환하는 매퍼
 */
public final class UserEntityMapper {
    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private UserEntityMapper() {
    }

    /**
     * UserEntity 객체를 User 도메인 모델로 변환합니다.
     * 입력된 UserEntity가 null이면 null을 반환하며, 연관된 토픽 ID 목록과 소개글 등 모든 주요 필드를 User 도메인 모델에 매핑합니다.
     *
     * @param userEntity 변환할 UserEntity 객체
     * @return 변환된 User 도메인 모델 객체 또는 입력이 null인 경우 null
     */
    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        List<Long> topicIds = Optional.ofNullable(userEntity.getUserTopicEntities())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(UserTopicEntity::getTopicId)
                .toList();

        return User.of(
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
                userEntity.getProfileImageUrl(),
                userEntity.getIntroductionText(),
                userEntity.isAdTermsAgreed(),
                userEntity.isDeleted()
        );
    }

    /**
     * User 도메인 모델 객체를 UserEntity(JPA 엔티티)로 변환합니다.
     *
     * User의 필드와 토픽 ID 목록을 기반으로 UserEntity와 연관된 UserTopicEntity 목록을 생성하여 연결합니다.
     * 입력이 null이면 null을 반환합니다.
     *
     * @param user 변환할 User 도메인 모델 객체
     * @return 변환된 UserEntity 객체 또는 입력이 null인 경우 null
     */
    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity userEntity =  UserEntity.of(
                user.getProvider(),
                user.getProviderId(),
                user.getRole(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getAuthorLevelId(),
                user.getOccupationId(),
                user.getVisitSourceId(),
                user.getProfileImageUrl(),
                user.getIntroductionText(),
                user.isAdTermsAgreed(),
                user.isDeleted()
        );

        // topicIds → userTopicEntities 변환 후 연결
        List<UserTopicEntity> userTopicEntities = Optional.ofNullable(user.getTopicIds())
                .orElseGet(Collections::emptyList)
                .stream()
                .distinct()
                .map(topicId -> UserTopicEntity.of(userEntity, topicId))
                .toList();
        userTopicEntities.forEach(userEntity::addUserTopic);

        return userEntity;
    }
}
