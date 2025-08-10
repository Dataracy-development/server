package com.dataracy.modules.dataset.application.dto.request.metadata;

/**
 *요청
 * @param dataId
 * @param fileUrl
 * @param originalFilename
 */
public record ParseMetadataRequest(
        Long dataId,
        String fileUrl,
        String originalFilename
) {}
