package com.dataracy.modules.dataset.application.port.out.command.update;

import com.dataracy.modules.dataset.domain.enums.MetadataParsingStatus;

/**
 * 데이터셋 메타데이터 파싱 상태를 업데이트하는 포트
 */
public interface UpdateMetadataParsingStatusPort {
  /**
   * 지정된 데이터의 메타데이터 파싱 상태를 업데이트합니다.
   *
   * @param dataId 데이터 ID
   * @param status 새로운 파싱 상태
   */
  void updateParsingStatus(Long dataId, MetadataParsingStatus status);
}

