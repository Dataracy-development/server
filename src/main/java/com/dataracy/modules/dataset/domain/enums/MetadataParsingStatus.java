package com.dataracy.modules.dataset.domain.enums;

/**
 * 데이터셋 메타데이터 파싱 상태를 나타내는 열거형
 *
 * <p>파일 업로드 후 비동기로 메타데이터를 파싱하는 과정의 상태를 추적합니다.
 */
public enum MetadataParsingStatus {
  /** 파싱 대기 중 (파일 업로드 완료, 파싱 시작 전) */
  PENDING,
  /** 파싱 진행 중 */
  PROCESSING,
  /** 파싱 완료 */
  COMPLETED,
  /** 파싱 실패 */
  FAILED
}

