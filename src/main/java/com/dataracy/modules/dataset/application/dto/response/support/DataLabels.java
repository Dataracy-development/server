package com.dataracy.modules.dataset.application.dto.response.support;

/**
 *요청
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
