package com.dataracy.modules.like.application.service.query;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.application.port.in.query.FindTargetIdsUseCase;
import com.dataracy.modules.like.application.port.in.validate.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.application.port.out.query.ReadLikePort;
import com.dataracy.modules.like.application.port.out.validate.ValidateLikePort;
import com.dataracy.modules.like.domain.enums.TargetType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeQueryService implements ValidateTargetLikeUseCase, FindTargetIdsUseCase {
  private final ReadLikePort readLikePort;
  private final ValidateLikePort validateLikePort;

  // Use Case 상수 정의
  private static final String VALIDATE_TARGET_LIKE_USE_CASE = "ValidateTargetLikeUseCase";
  private static final String FIND_TARGET_IDS_USE_CASE = "FindTargetIdsUseCase";

  /**
   * 사용자가 특정 대상에 좋아요를 눌렀는지 여부를 반환합니다.
   *
   * @param userId 좋아요 여부를 확인할 사용자 ID
   * @param targetId 확인할 대상의 ID
   * @param targetType 대상의 타입
   * @return 사용자가 해당 대상을 좋아요했다면 true, 아니면 false
   */
  @Override
  @Transactional(readOnly = true)
  public boolean hasUserLikedTarget(Long userId, Long targetId, TargetType targetType) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                VALIDATE_TARGET_LIKE_USE_CASE,
                "사용자가 특정 대상에 좋아요를 눌렀는지 여부 반환 서비스 시작 targetType="
                    + targetType
                    + ", targetId="
                    + targetId);
    boolean hasUser = validateLikePort.isLikedTarget(userId, targetId, targetType);
    LoggerFactory.service()
        .logSuccess(
            VALIDATE_TARGET_LIKE_USE_CASE,
            "사용자가 특정 대상에 좋아요를 눌렀는지 여부 반환 서비스 종료 targetType="
                + targetType
                + ", targetId="
                + targetId,
            startTime);
    return hasUser;
  }

  /**
   * 사용자가 지정한 대상 타입과 대상 ID 목록 중에서 좋아요를 누른 대상의 ID 목록을 반환합니다.
   *
   * @param userId 좋아요 여부를 확인할 사용자 ID
   * @param targetIds 확인할 대상 ID 목록
   * @param targetType 대상의 타입
   * @return 사용자가 좋아요를 누른 대상 ID 목록
   */
  @Override
  @Transactional(readOnly = true)
  public List<Long> findLikedTargetIds(Long userId, List<Long> targetIds, TargetType targetType) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                FIND_TARGET_IDS_USE_CASE,
                "사용자가 지정한 대상 타입과 대상 ID 목록 중에서 좋아요를 누른 대상의 ID 목록을 반환 서비스 시작 targetType="
                    + targetType);
    List<Long> likedTargetIds = readLikePort.findLikedTargetIds(userId, targetIds, targetType);
    LoggerFactory.service()
        .logSuccess(
            FIND_TARGET_IDS_USE_CASE,
            "사용자가 지정한 대상 타입과 대상 ID 목록 중에서 좋아요를 누른 대상의 ID 목록을 반환 서비스 종료 targetType=" + targetType,
            startTime);
    return likedTargetIds;
  }
}
