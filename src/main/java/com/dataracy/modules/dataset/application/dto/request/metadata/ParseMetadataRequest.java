package com.dataracy.modules.dataset.application.dto.request.metadata;

public record ParseMetadataRequest(
        Long dataId,
        String fileUrl,
        String originalFilename
) {}
