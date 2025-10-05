package com.dataracy.modules.dataset.application.port.in.command.metadata;

import com.dataracy.modules.dataset.application.dto.request.metadata.ParseMetadataRequest;

public interface ParseMetadataUseCase {
  /**
   * 주어진 요청 정보를 바탕으로 메타데이터를 파싱하여 저장합니다.
   *
   * @param request 메타데이터 파싱과 저장에 필요한 정보를 포함하는 요청 객체
   */
  void parseAndSaveMetadata(ParseMetadataRequest request);
}
