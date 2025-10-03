/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.behaviorlog.application.service.command;

import org.springframework.stereotype.Service;

import com.dataracy.modules.behaviorlog.application.port.in.MergeAnonymousUserLogUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogMergePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 익명 → 유저 행동 로그 병합 서비스 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MergeAnonymousUserLogService implements MergeAnonymousUserLogUseCase {

  private final BehaviorLogMergePort mergePort;

  /**
   * 익명 사용자의 행동 로그를 지정된 사용자 계정으로 병합합니다.
   *
   * @param anonymousId 병합할 익명 사용자의 식별자
   * @param userId 병합 대상이 되는 등록된 사용자 ID
   */
  @Override
  public void merge(String anonymousId, Long userId) {
    log.info("익명 사용자 로그 병합 시작: anonymousId={}, userId={}", anonymousId, userId);
    mergePort.merge(anonymousId, userId);
    log.info("익명 사용자 로그 병합 완료: anonymousId={}, userId={}", anonymousId, userId);
  }
}
