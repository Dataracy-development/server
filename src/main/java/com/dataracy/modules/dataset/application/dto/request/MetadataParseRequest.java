package com.dataracy.modules.dataset.application.dto.request;

public record MetadataParseRequest(
        Long dataId,
        String fileUrl,
        String originalFilename
) {}
