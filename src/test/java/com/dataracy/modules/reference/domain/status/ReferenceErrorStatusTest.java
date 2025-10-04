package com.dataracy.modules.reference.domain.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/** ReferenceErrorStatus 테스트 */
class ReferenceErrorStatusTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  @Test
  @DisplayName("NOT_FOUND_TOPIC_NAME 상태 확인")
  void notFoundTopicNameStatusCheck() {
    // when
    ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME;

    // then
    assertAll(
        () -> assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(status.getCode()).isEqualTo("REFERENCE-001"),
        () -> assertThat(status.getMessage()).isEqualTo("해당하는 토픽명이 없습니다. 올바른 값을 입력해주세요."));
  }

  @Test
  @DisplayName("NOT_FOUND_AUTHOR_LEVEL 상태 확인")
  void notFoundAuthorLevelStatusCheck() {
    // when
    ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_AUTHOR_LEVEL;

    // then
    assertAll(
        () -> assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(status.getCode()).isEqualTo("REFERENCE-002"),
        () -> assertThat(status.getMessage()).isEqualTo("해당 작성자 유형이 존재하지 않습니다."));
  }

  @Test
  @DisplayName("NOT_FOUND_OCCUPATION 상태 확인")
  void notFoundOccupationStatusCheck() {
    // when
    ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_OCCUPATION;

    // then
    assertAll(
        () -> assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(status.getCode()).isEqualTo("REFERENCE-003"),
        () -> assertThat(status.getMessage()).isEqualTo("해당 직업이 존재하지 않습니다."));
  }

  @Test
  @DisplayName("NOT_FOUND_VISIT_SOURCE 상태 확인")
  void notFoundVisitSourceStatusCheck() {
    // when
    ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_VISIT_SOURCE;

    // then
    assertAll(
        () -> assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(status.getCode()).isEqualTo("REFERENCE-004"),
        () -> assertThat(status.getMessage()).isEqualTo("해당 방문 경로가 존재하지 않습니다."));
  }

  @Test
  @DisplayName("NOT_FOUND_ANALYSIS_PURPOSE 상태 확인")
  void notFoundAnalysisPurposeStatusCheck() {
    // when
    ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE;

    // then
    assertAll(
        () -> assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(status.getCode()).isEqualTo("REFERENCE-005"),
        () -> assertThat(status.getMessage()).isEqualTo("해당 분석 목적이 존재하지 않습니다."));
  }

  @Test
  @DisplayName("NOT_FOUND_DATA_SOURCE 상태 확인")
  void notFoundDataSourceStatusCheck() {
    // when
    ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_DATA_SOURCE;

    // then
    assertAll(
        () -> assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(status.getCode()).isEqualTo("REFERENCE-006"),
        () -> assertThat(status.getMessage()).isEqualTo("해당 데이터 출처가 존재하지 않습니다."));
  }

  @Test
  @DisplayName("NOT_FOUND_DATA_TYPE 상태 확인")
  void notFoundDataTypeStatusCheck() {
    // when
    ReferenceErrorStatus status = ReferenceErrorStatus.NOT_FOUND_DATA_TYPE;

    // then
    assertAll(
        () -> assertThat(status.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(status.getCode()).isEqualTo("REFERENCE-007"),
        () -> assertThat(status.getMessage()).isEqualTo("해당 데이터 유형이 존재하지 않습니다."));
  }
}
