package com.dataracy.modules.project.domain.status;

import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseSuccessCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectSuccessStatus implements BaseSuccessCode {
  CREATED_PROJECT(HttpStatus.CREATED, "201", "제출이 완료되었습니다"),
  FIND_REAL_TIME_PROJECTS(HttpStatus.OK, "200", "실시간 프로젝트 리스트를 조회하였습니다."),
  FIND_SIMILAR_PROJECTS(HttpStatus.OK, "200", "유사 프로젝트 리스트를 조회하였습니다."),
  FIND_POPULAR_PROJECTS(HttpStatus.OK, "200", "인기있는 프로젝트 리스트를 조회하였습니다."),
  FIND_FILTERED_PROJECTS(HttpStatus.OK, "200", "필터링된 프로젝트 리스트를 조회하였습니다."),
  GET_PROJECT_DETAIL(HttpStatus.OK, "200", "프로젝트 상세 정보를 조회하였습니다."),
  GET_CONTINUE_PROJECTS(HttpStatus.OK, "200", "해당하는 프로젝트의 이어가기 프로젝트 리스트를 조회하였습니다."),
  GET_CONNECTED_PROJECTS_ASSOCIATED_DATA(HttpStatus.OK, "200", "해당하는 데이터셋을 이용한 프로젝트 리스트를 조회하였습니다."),
  MODIFY_PROJECT(HttpStatus.OK, "200", "프로젝트 수정이 완료되었습니다."),
  DELETE_PROJECT(HttpStatus.OK, "200", "프로젝트 삭제가 완료되었습니다."),
  RESTORE_PROJECT(HttpStatus.OK, "200", "프로젝트 복원에 완료되었습니다."),
  GET_USER_PROJECTS(HttpStatus.OK, "200", "로그인한 회원이 업로드한 프로젝트 목록 조회가 완료되었습니다."),
  GET_LIKE_PROJECTS(HttpStatus.OK, "200", "로그인한 회원이 좋아요한 프로젝트 목록 조회가 완료되었습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
