package com.dataracy.modules.user.adapter.jpa.impl.query;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.mapper.UserEntityMapper;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryDbAdapter implements UserQueryPort {
  private final UserJpaRepository userJpaRepository;

  // Entity 상수 정의
  private static final String USER_ENTITY = "UserEntity";

  /**
   * 주어진 사용자 ID로 사용자를 조회하여 Optional<User>로 반환합니다.
   *
   * @param userId 조회할 사용자의 고유 ID
   * @return 사용자가 존재하면 해당 User 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
   */
  @Override
  public Optional<User> findUserById(Long userId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(USER_ENTITY, "[findById] 유저 아이디로 유저 조회 시작 userId=" + userId);
    Optional<UserEntity> userEntity = userJpaRepository.findById(userId);
    LoggerFactory.db()
        .logQueryEnd(USER_ENTITY, "[findById] 유저 아이디로 유저 조회 종료 userId=" + userId, startTime);
    return userEntity.map(UserEntityMapper::toDomain);
  }

  /**
   * 소셜 제공자에서 발급한 ID로 사용자를 조회합니다.
   *
   * @param providerId 소셜 제공자에서 발급한 사용자 ID
   * @return 해당 providerId에 해당하는 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()
   */
  @Override
  public Optional<User> findUserByProviderId(String providerId) {
    Instant startTime =
        LoggerFactory.db().logQueryStart(USER_ENTITY, "[findByProviderId] 소셜 제공자 아이디로 유저 조회 시작");
    Optional<UserEntity> userEntity = userJpaRepository.findByProviderId(providerId);
    LoggerFactory.db()
        .logQueryEnd(USER_ENTITY, "[findByProviderId] 소셜 제공자 아이디로 유저 조회 종료", startTime);
    return userEntity.map(UserEntityMapper::toDomain);
  }

  /**
   * 이메일로 사용자를 조회하여 Optional<User>로 반환합니다.
   *
   * @param email 조회할 사용자의 이메일 주소
   * @return 해당 이메일을 가진 사용자가 존재하면 Optional<User>, 없으면 Optional.empty()
   */
  @Override
  public Optional<User> findUserByEmail(String email) {
    Instant startTime =
        LoggerFactory.db().logQueryStart(USER_ENTITY, "[findByEmail] 이메일로 유저 조회 시작 email=" + email);
    Optional<UserEntity> userEntity = userJpaRepository.findByEmail(email);
    LoggerFactory.db()
        .logQueryEnd(USER_ENTITY, "[findByEmail] 이메일로 유저 조회 종료 email=" + email, startTime);
    return userEntity.map(UserEntityMapper::toDomain);
  }

  @Override
  public Optional<String> findNicknameById(Long userId) {
    Instant startTime =
        LoggerFactory.db()
            .logQueryStart(USER_ENTITY, "[findNicknameById] 유저 아이디로 닉네임 조회 시작 userId=" + userId);
    Optional<String> nickname = userJpaRepository.findNicknameById(userId);
    LoggerFactory.db()
        .logQueryEnd(
            USER_ENTITY, "[findNicknameById] 유저 아이디로 닉네임 조회 종료 userId=" + userId, startTime);
    return nickname;
  }
}
