package com.dataracy.modules.dataset.application.dto.response.support;

/**
 *
 * @param topicLabel
 * @param dataSourceLabel
 * @param dataTypeLabel
 * @param username
 */
public record DataLabels(
        String topicLabel,
        String dataSourceLabel,
        String dataTypeLabel,
        String username
) {}
