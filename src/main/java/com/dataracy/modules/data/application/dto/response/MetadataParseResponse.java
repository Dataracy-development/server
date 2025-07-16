package com.dataracy.modules.data.application.dto.response;

public record MetadataParseResponse(
        int rowCount,
        int columnCount,
        String previewJson
) {}
