package com.dataracy.modules.filestorage.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("S3UploadException 테스트")
class S3UploadExceptionTest {

  @Test
  @DisplayName("S3UploadException 생성 및 속성 확인")
  void s3UploadExceptionShouldHaveCorrectProperties() {
    // Given
    String message = "S3 파일 업로드에 실패했습니다.";

    // When
    S3UploadException exception = new S3UploadException(message);

    // Then
    assertAll(
        () -> assertThat(exception.getMessage()).isEqualTo(message),
        () -> assertThat(exception.getCause()).isNull());
  }

  @Test
  @DisplayName("S3UploadException - 메시지와 원인으로 생성")
  void s3UploadExceptionShouldCreateWithMessageAndCause() {
    // Given
    String message = "S3 업로드 중 오류 발생";
    RuntimeException cause = new RuntimeException("네트워크 오류");

    // When
    S3UploadException exception = new S3UploadException(message, cause);

    // Then
    assertAll(
        () -> assertThat(exception.getMessage()).isEqualTo(message),
        () -> assertThat(exception.getCause()).isEqualTo(cause));
  }

  @Test
  @DisplayName("S3UploadException은 RuntimeException을 상속받는다")
  void s3UploadExceptionShouldExtendRuntimeException() {
    // Given
    String message = "S3 업로드 실패";

    // When
    S3UploadException exception = new S3UploadException(message);

    // Then
    assertThat(exception).isInstanceOf(RuntimeException.class).isInstanceOf(Exception.class);
  }
}
