package com.dataracy.modules.user.infra.mapper;

import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.infra.jpa.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity userEntity) {
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
                userEntity.getDomains(),
                userEntity.getVisitSource(),
                userEntity.isAdTermsAgreed(),
                userEntity.isDeleted()
        );
    }

    public static UserEntity toEntity(User user) {
        return UserEntity.toEntity(
                user.getId(),
                user.getProvider(),
                user.getProviderId(),
                user.getRole(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getAuthorLevel(),
                user.getOccupation(),
                user.getDomains(),
                user.getVisitSource(),
                user.isAdTermsAgreed(),
                user.isDeleted()
        );
    }
}
