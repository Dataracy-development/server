package com.dataracy.modules.dataset.application.dto.response.read;

public record DataGroupCountResponse(
        Long topicId,
        String topicLabel,
        Long count
) {}
