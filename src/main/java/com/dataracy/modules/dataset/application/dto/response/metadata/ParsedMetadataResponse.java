package com.dataracy.modules.dataset.application.dto.response.metadata;

/**
 *요청
 * @param rowCount
 * @param columnCount
 * @param previewJson
 */
public record ParsedMetadataResponse(
        int rowCount,
        int columnCount,
        String previewJson
) {}
