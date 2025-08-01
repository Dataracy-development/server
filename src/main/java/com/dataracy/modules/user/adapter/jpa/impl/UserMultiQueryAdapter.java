package com.dataracy.modules.user.adapter.jpa.impl;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.application.port.out.jpa.UserMultiQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserMultiQueryAdapter implements UserMultiQueryPort {
    private final UserJpaRepository userJpaRepository;

    /**
     * 주어진 사용자 ID 목록에 해당하는 사용자들의 닉네임을 사용자 ID별로 매핑하여 반환합니다.
     *
     * @param userIds 조회할 사용자 ID 목록
     * @return 각 사용자 ID와 해당 닉네임이 매핑된 맵
     */
    @Override
    public Map<Long, String> findUsernamesByIds(List<Long> userIds) {
        List<UserEntity> userEntities = findUserEntitiesWithLogging(userIds, "[findAllById] 유저 아이디로 유저 목록 조회");
        return userEntities
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getNickname));
    }

    /**
     * 주어진 사용자 ID 목록에 대해 각 사용자의 프로필 이미지 URL을 맵으로 반환합니다.
     *
     * @param userIds 프로필 이미지 URL을 조회할 사용자 ID 목록
     * @return 사용자 ID를 키로 하고, 프로필 이미지 URL(없을 경우 빈 문자열)을 값으로 하는 맵
     */
    @Override
    public Map<Long, String> findUserThumbnailsByIds(List<Long> userIds) {
        List<UserEntity> userEntities = findUserEntitiesWithLogging(userIds, "[findAllById] 유저 아이디로 유저 목록 조회");
        return userEntities
                .stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        user -> Optional.ofNullable(user.getProfileImageUrl()).orElse("")
                ));
    }

    /**
     * 주어진 사용자 ID 목록에 대해 각 사용자의 author level ID를 문자열로 반환합니다.
     *
     * author level ID가 없는 경우 기본값 "1"이 반환됩니다.
     *
     * @param userIds 조회할 사용자 ID 목록
     * @return 사용자 ID를 키로 하고 author level ID(문자열)를 값으로 하는 맵
     */
    @Override
    public Map<Long, String> findUserAuthorLevelIds(List<Long> userIds) {
        List<UserEntity> userEntities = findUserEntitiesWithLogging(userIds, "[findAllById] 유저 아이디로 유저 목록 조회");
        return userEntities
                .stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        user -> String.valueOf(Optional.ofNullable(user.getAuthorLevelId()).orElse(1L))
                ));
    }

    private List<UserEntity> findUserEntitiesWithLogging(List<Long> userIds, String operation) {
        Instant startTime = LoggerFactory.db().logQueryStart("UserEntity", operation + " 시작");
        List<UserEntity> userEntities = userJpaRepository.findAllById(userIds);
        LoggerFactory.db().logQueryEnd("UserEntity", operation + " 종료", startTime);
        return userEntities;
    }
}
