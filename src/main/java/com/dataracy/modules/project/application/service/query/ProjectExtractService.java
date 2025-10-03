/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.service.query;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.in.query.extractor.FindUserIdIncludingDeletedUseCase;
import com.dataracy.modules.project.application.port.in.query.extractor.FindUserIdUseCase;
import com.dataracy.modules.project.application.port.out.query.extractor.ExtractProjectOwnerPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectExtractService implements FindUserIdUseCase, FindUserIdIncludingDeletedUseCase {
  private final ExtractProjectOwnerPort extractProjectOwnerPort;

  // Use Case 상수 정의
  private static final String FIND_USER_ID_USE_CASE = "FindUserIdUseCase";
  private static final String FIND_USER_ID_INCLUDING_DELETED_USE_CASE =
      "FindUserIdIncludingDeletedUseCase";

  /**
   * 주어진 프로젝트 ID에 해당하는 소유자(사용자) ID를 조회합니다.
   *
   * @param projectId 사용자 ID를 조회할 프로젝트의 ID
   * @return 해당 프로젝트의 소유자(사용자) ID
   */
  @Override
  @Transactional(readOnly = true)
  public Long findUserIdByProjectId(Long projectId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(FIND_USER_ID_USE_CASE, "프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + projectId);
    Long userId = extractProjectOwnerPort.findUserIdByProjectId(projectId);
    LoggerFactory.service()
        .logSuccess(
            FIND_USER_ID_USE_CASE,
            "프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + projectId,
            startTime);
    return userId;
  }

  /**
   * 삭제된 프로젝트를 포함하여 주어진 프로젝트 ID의 소유자 사용자 ID를 조회합니다.
   *
   * @param projectId 조회할 프로젝트의 ID
   * @return 해당 프로젝트(삭제된 경우 포함)의 소유자 사용자 ID
   */
  @Override
  @Transactional(readOnly = true)
  public Long findUserIdIncludingDeleted(Long projectId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                FIND_USER_ID_INCLUDING_DELETED_USE_CASE,
                "삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + projectId);
    Long userId = extractProjectOwnerPort.findUserIdIncludingDeleted(projectId);
    LoggerFactory.service()
        .logSuccess(
            FIND_USER_ID_INCLUDING_DELETED_USE_CASE,
            "삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + projectId,
            startTime);
    return userId;
  }
}
