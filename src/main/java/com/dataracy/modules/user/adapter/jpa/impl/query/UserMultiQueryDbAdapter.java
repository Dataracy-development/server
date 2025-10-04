package com.dataracy.modules.user.adapter.jpa.impl.query;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.application.port.out.query.UserMultiQueryPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserMultiQueryDbAdapter implements UserMultiQueryPort {
  private final UserJpaRepository userJpaRepository;

  // Entity 상수 정의
  private static final String USER_ENTITY = "UserEntity";

  /**
   * 주어진 사용자 ID 목록에 대해 각 사용자 ID와 해당 사용자의 닉네임을 매핑한 맵을 반환합니다.
   *
   * @param userIds 닉네임을 조회할 사용자 ID 목록
   * @return 사용자 ID를 키로 하고 닉네임을 값으로 하는 맵
   */
  @Override
  public Map<Long, String> findUsernamesByIds(List<Long> userIds) {

    List<UserEntity> userEntities =
        findUserEntitiesWithLogging(userIds, "[findAllById] 유저 아이디로 유저 목록 조회");
    return userEntities.stream()
        .collect(Collectors.toMap(UserEntity::getId, UserEntity::getNickname));
  }

  /**
   * 주어진 사용자 ID 목록에 대해 각 사용자의 프로필 이미지 URL을 반환합니다. 사용자 ID를 키로, 해당 사용자의 프로필 이미지 URL을 값으로 하는 맵을 반환합니다.
   * 프로필 이미지가 없는 경우 빈 문자열로 매핑됩니다.
   *
   * @param userIds 프로필 이미지 URL을 조회할 사용자 ID 목록
   * @return 사용자 ID별 프로필 이미지 URL 맵 (이미지가 없으면 빈 문자열)
   */
  @Override
  public Map<Long, String> findUserThumbnailsByIds(List<Long> userIds) {
    List<UserEntity> userEntities =
        findUserEntitiesWithLogging(userIds, "[findAllById] 유저 아이디로 유저 목록 조회");
    return userEntities.stream()
        .collect(
            Collectors.toMap(
                UserEntity::getId,
                user -> Optional.ofNullable(user.getProfileImageUrl()).orElse("")));
  }

  /**
   * 주어진 사용자 ID 목록에 대해 각 사용자의 author level ID를 문자열로 매핑하여 반환합니다. author level ID가 null인 경우 "1"로
   * 대체됩니다.
   *
   * @param userIds 조회할 사용자 ID 목록
   * @return 사용자 ID를 키, author level ID(문자열)를 값으로 하는 맵
   */
  @Override
  public Map<Long, String> findUserAuthorLevelIds(List<Long> userIds) {
    List<UserEntity> userEntities =
        findUserEntitiesWithLogging(userIds, "[findAllById] 유저 아이디로 유저 목록 조회");
    return userEntities.stream()
        .collect(
            Collectors.toMap(
                UserEntity::getId,
                user -> String.valueOf(Optional.ofNullable(user.getAuthorLevelId()).orElse(1L))));
  }

  /**
   * 지정된 사용자 ID 목록에 해당하는 UserEntity 객체들을 조회하고, 조회 작업의 시작과 종료를 로깅합니다.
   *
   * @param userIds 조회할 사용자 ID 목록
   * @param operation 로깅에 사용할 작업명
   * @return 조회된 UserEntity 객체들의 리스트
   */
  private List<UserEntity> findUserEntitiesWithLogging(List<Long> userIds, String operation) {
    Instant startTime = LoggerFactory.db().logQueryStart(USER_ENTITY, operation + " 시작");
    List<UserEntity> userEntities = userJpaRepository.findAllById(userIds);
    LoggerFactory.db().logQueryEnd(USER_ENTITY, operation + " 종료", startTime);
    return userEntities;
  }
}
