package com.dataracy.modules.user.adapter.jpa.impl.command;

import java.util.List;

import org.springframework.stereotype.Repository;

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

@Repository
@RequiredArgsConstructor
public class UserCommandDbAdapter implements UserCommandPort {
  private final UserJpaRepository userJpaRepository;

  // Entity 상수 정의
  private static final String USER_ENTITY = "UserEntity";
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
    LoggerFactory.db().logSave(USER_ENTITY, String.valueOf(savedUser.getId()), "DB에 유저를 저장하였습니다.");
    return UserEntityMapper.toDomain(savedUser);
  }

  /**
   * 지정한 사용자의 비밀번호를 새로운 인코딩된 값으로 업데이트합니다.
   *
   * <p>사용자 엔티티가 존재하지 않으면 UserErrorStatus.NOT_FOUND_USER를 가진 UserException을 던집니다.
   *
   * @param userId 변경 대상 사용자의 ID
   * @param encodePassword 새로 설정할 인코딩된 비밀번호
   * @throws UserException 사용자를 찾을 수 없을 경우(조회 실패)
   */
  @Override
  public void changePassword(Long userId, String encodePassword) {
    UserEntity userEntity =
        userJpaRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db()
                      .logWarning(USER_ENTITY, "[비밀번호 변경] 사용자를 찾을 수 없습니다. userId=" + userId);
                  return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
    userEntity.changePassword(encodePassword);
    LoggerFactory.db()
        .logUpdate(USER_ENTITY, String.valueOf(userEntity.getId()), "새 비밀번호를 변경하였습니다.");
  }

  /**
   * 사용자의 정보를 수정하고 관심 주제(토픽) 연관 정보를 갱신한다.
   *
   * <p>요청한 사용자 ID의 엔티티를 조회하여 존재하지 않으면 UserException(UserErrorStatus.NOT_FOUND_USER)를 던진다. 조회된 엔티티에
   * 요청 DTO의 값으로 사용자 정보를 적용(modifyUserInfo)하고, 기존에 저장된 모든 관심 주제 레코드를 삭제한 뒤 요청 DTO에 포함된 topicIds로 새
   * 관심 주제를 생성·저장하고 최종적으로 사용자 엔티티를 저장한다.
   *
   * @param userId 수정할 사용자의 식별자
   * @param requestDto 사용자 정보 및 관심 주제 ID 목록을 담은 요청 DTO
   * @throws UserException 조회된 사용자가 없을 경우 UserErrorStatus.NOT_FOUND_USER 상태로 발생
   */
  @Override
  public void modifyUserInfo(Long userId, ModifyUserInfoRequest requestDto) {
    UserEntity userEntity =
        userJpaRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db()
                      .logWarning(USER_ENTITY, "[회원 정보 수정] 사용자를 찾을 수 없습니다. userId=" + userId);
                  return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
    userEntity.modifyUserInfo(requestDto);

    // 기존 관심 주제 전부 삭제 (쿼리 직접)
    userTopicJpaRepository.deleteAllByUserId(userId);

    // 새 관심 주제 추가 (배치 처리로 N+1 문제 해결)
    List<UserTopicEntity> topicEntities =
        requestDto.topicIds().stream()
            .map(
                topicId -> {
                  UserTopicEntity topicEntity = UserTopicEntity.of(userEntity, topicId);
                  userEntity.addUserTopic(topicEntity);
                  return topicEntity;
                })
            .toList();

    // 배치로 한 번에 저장 (N+1 문제 해결)
    userTopicJpaRepository.saveAll(topicEntities);
    userJpaRepository.save(userEntity);
  }

  /**
   * 지정한 사용자의 프로필 이미지 URL을 갱신하고 변경된 사용자를 저장합니다.
   *
   * @param userId 갱신할 사용자의 식별자
   * @param profileImageFileUrl 새 프로필 이미지 파일의 URL
   * @throws UserException 사용자 식별자로 조회된 레코드가 없을 경우 {@code UserErrorStatus.NOT_FOUND_USER} 상태로 발생합니다.
   */
  @Override
  public void updateProfileImageFile(Long userId, String profileImageFileUrl) {
    UserEntity userEntity =
        userJpaRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db()
                      .logWarning(USER_ENTITY, "[회원 정보 수정] 사용자를 찾을 수 없습니다. userId=" + userId);
                  return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
    userEntity.modifyProfileImageUrl(profileImageFileUrl);
    userJpaRepository.save(userEntity);
  }

  /**
   * 지정된 사용자 ID의 사용자를 데이터베이스상에서 탈퇴 상태로 변경합니다.
   *
   * @param userId 탈퇴 처리할 사용자의 고유 ID
   */
  @Override
  public void withdrawalUser(Long userId) {
    userJpaRepository.withdrawalUser(userId);
    LoggerFactory.db().logUpdate(USER_ENTITY, String.valueOf(userId), "해당 사용자를 탈퇴 처리한다.");
  }
}
