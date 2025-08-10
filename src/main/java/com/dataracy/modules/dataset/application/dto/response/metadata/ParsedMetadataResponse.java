package com.dataracy.modules.dataset.application.dto.response.metadata;

/**
 *
 * @param rowCount
 * @param columnCount
 * @param previewJson
 */
public record ParsedMetadataResponse(
        int rowCount,
        int columnCount,
        String previewJson
) {}
