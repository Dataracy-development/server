package com.dataracy.modules.dataset.application.dto.request.metadata;

/**
 *
 * @param dataId
 * @param fileUrl
 * @param originalFilename
 */
public record ParseMetadataRequest(
        Long dataId,
        String fileUrl,
        String originalFilename
) {}
