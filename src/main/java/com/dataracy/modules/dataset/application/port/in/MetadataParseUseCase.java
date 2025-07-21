package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.request.MetadataParseRequest;

public interface MetadataParseUseCase {
    /**
 * 주어진 요청을 기반으로 메타데이터를 파싱하고 저장합니다.
 *
 * @param request 메타데이터 파싱 및 저장에 필요한 정보를 담은 요청 객체
 */
void parseAndSaveMetadata(MetadataParseRequest request);
}
