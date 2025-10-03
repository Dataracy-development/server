/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;

@DisplayName("BusinessException 테스트")
class BusinessExceptionTest {

  @Test
  @DisplayName("DataException - BusinessException 상속 테스트")
  void dataExceptionInheritance() {
    // given
    DataException exception = new DataException(DataErrorStatus.NOT_FOUND_DATA);

    // when & then
    assertAll(
        () -> assertThat(exception).isInstanceOf(BusinessException.class),
        () -> assertThat(exception).isInstanceOf(CustomException.class),
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(exception.getCode()).isEqualTo("DATA-002"),
        () -> assertThat(exception.getMessage()).isEqualTo("해당 데이터셋 리소스가 존재하지 않습니다."),
        () -> assertThat(exception.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA));
  }

  @Test
  @DisplayName("ProjectException - BusinessException 상속 테스트")
  void projectExceptionInheritance() {
    // given
    ProjectException exception = new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);

    // when & then
    assertAll(
        () -> assertThat(exception).isInstanceOf(BusinessException.class),
        () -> assertThat(exception).isInstanceOf(CustomException.class),
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(exception.getCode()).isEqualTo("PROJECT-002"),
        () -> assertThat(exception.getMessage()).isEqualTo("해당 프로젝트 리소스가 존재하지 않습니다."),
        () -> assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT));
  }

  @Test
  @DisplayName("DataException - INVALID_FILE_URL")
  void dataExceptionInvalidFileUrl() {
    // given
    DataException exception = new DataException(DataErrorStatus.INVALID_FILE_URL);

    // when & then
    assertAll(
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
        () -> assertThat(exception.getCode()).isEqualTo("DATA-010"),
        () -> assertThat(exception.getMessage()).isEqualTo("유효하지 않은 파일 url입니다."),
        () -> assertThat(exception.getErrorCode()).isEqualTo(DataErrorStatus.INVALID_FILE_URL));
  }

  @Test
  @DisplayName("ProjectException - INVALID_THUMBNAIL_FILE_URL")
  void projectExceptionInvalidThumbnailFileUrl() {
    // given
    ProjectException exception =
        new ProjectException(ProjectErrorStatus.INVALID_THUMBNAIL_FILE_URL);

    // when & then
    assertAll(
        () -> assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
        () -> assertThat(exception.getCode()).isEqualTo("PROJECT-010"),
        () -> assertThat(exception.getMessage()).isEqualTo("유효하지 않은 프로젝트 썸네일 파일 url입니다."),
        () ->
            assertThat(exception.getErrorCode())
                .isEqualTo(ProjectErrorStatus.INVALID_THUMBNAIL_FILE_URL));
  }
}
