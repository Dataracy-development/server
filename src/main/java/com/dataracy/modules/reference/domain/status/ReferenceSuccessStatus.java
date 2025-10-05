package com.dataracy.modules.reference.domain.status;

import org.springframework.http.HttpStatus;

import com.dataracy.modules.common.status.BaseSuccessCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReferenceSuccessStatus implements BaseSuccessCode {
  OK_TOTAL_TOPIC_LIST(HttpStatus.OK, "200", "도메인 리스트 조회에 성공했습니다."),
  OK_TOTAL_AUTHOR_LEVEL_LIST(HttpStatus.OK, "200", "작성자 유형 리스트 조회에 성공했습니다."),
  OK_TOTAL_OCCUPATION_LIST(HttpStatus.OK, "200", "직업 리스트 조회에 성공했습니다."),
  OK_TOTAL_VISIT_SOURCE_LIST(HttpStatus.OK, "200", "방문 경로 리스트 조회에 성공했습니다."),
  OK_TOTAL_ANALYSIS_PURPOSE_LIST(HttpStatus.OK, "200", "분석 목적 리스트 조회에 성공했습니다."),
  OK_TOTAL_DATA_SOURCE_LIST(HttpStatus.OK, "200", "데이터 출처 리스트 조회에 성공했습니다."),
  OK_TOTAL_DATA_TYPE_LIST(HttpStatus.OK, "200", "데이터 유형 리스트 조회에 성공했습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
