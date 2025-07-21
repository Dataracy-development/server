package com.dataracy.modules.dataset.application.dto.response;

public record MetadataParseResponse(
        int rowCount,
        int columnCount,
        String previewJson
) {}
