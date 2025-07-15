package com.dataracy.modules.data.application.port.in;

import com.dataracy.modules.data.application.dto.request.MetadataParseRequest;

public interface MetadataParseUseCase {
    void parseAndSaveMetadata(MetadataParseRequest request);
}
