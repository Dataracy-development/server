package com.dataracy.modules.data.application.dto.request;

public record MetadataParseRequest(
        Long dataId,
        String fileUrl,
        String originalFilename
) {}
