/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.domain.status;

import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectErrorStatus implements BaseErrorCode {
  FAIL_SAVE_PROJECT(HttpStatus.INTERNAL_SERVER_ERROR, "PROJECT-001", "프로젝트 업로드에 실패했습니다."),
  NOT_FOUND_PROJECT(HttpStatus.NOT_FOUND, "PROJECT-002", "해당 프로젝트 리소스가 존재하지 않습니다."),
  NOT_FOUND_PARENT_PROJECT(HttpStatus.NOT_FOUND, "PROJECT-003", "해당 부모 프로젝트 리소스가 존재하지 않습니다."),
  FAIL_REAL_TIME_SEARCH_PROJECT(
      HttpStatus.INTERNAL_SERVER_ERROR, "PROJECT-004", "프로젝트 자동완성 검색에 실패"),
  FAIL_INDEXING_PROJECT(HttpStatus.INTERNAL_SERVER_ERROR, "PROJECT-005", "Elasticsearch 색인 실패"),
  FAIL_SIMILAR_SEARCH_PROJECT(HttpStatus.INTERNAL_SERVER_ERROR, "PROJECT-006", "유사 프로젝트 검색 실패"),
  INVALID_PROJECT_SORT_TYPE(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "PROJECT-007",
      "프로젝트 정렬은 LATEST, OLDEST, MOST_LIKED, MOST_VIEWED, MOST_COMMENTED, LEAST_COMMENTED 만 가능합니다."),
  NOT_MATCH_CREATOR(HttpStatus.FORBIDDEN, "PROJECT-008", "작성자만 수정 및 삭제, 복원이 가능합니다."),
  FAIL_GET_USER_INFO(
      HttpStatus.INTERNAL_SERVER_ERROR, "PROJECT-009", "ProjectUser을 생성하기 위한 유저 정보가 주입되지 않았습니다."),
  INVALID_THUMBNAIL_FILE_URL(HttpStatus.BAD_REQUEST, "PROJECT-010", "유효하지 않은 프로젝트 썸네일 파일 url입니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
