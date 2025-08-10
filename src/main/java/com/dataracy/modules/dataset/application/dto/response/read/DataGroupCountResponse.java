package com.dataracy.modules.dataset.application.dto.response.read;

/**
 *
 * @param topicId
 * @param topicLabel
 * @param count
 */
public record DataGroupCountResponse(
        Long topicId,
        String topicLabel,
        Long count
) {}
