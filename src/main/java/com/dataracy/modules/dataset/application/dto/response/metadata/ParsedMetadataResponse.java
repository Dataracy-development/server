package com.dataracy.modules.dataset.application.dto.response.metadata;

public record ParsedMetadataResponse(
        int rowCount,
        int columnCount,
        String previewJson
) {}
