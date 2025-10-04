package com.dataracy.modules.filestorage.domain.exception;

public class S3UploadException extends RuntimeException {
  /**
   * S3 업로드 작업 중 발생한 예외를 생성합니다.
   *
   * @param message 예외에 대한 설명 메시지
   * @param cause 예외의 근본 원인
   */
  public S3UploadException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * S3 업로드 작업 중 발생한 예외를 메시지와 함께 생성합니다.
   *
   * @param message 예외에 대한 설명 메시지
   */
  public S3UploadException(String message) {
    super(message);
  }
}
