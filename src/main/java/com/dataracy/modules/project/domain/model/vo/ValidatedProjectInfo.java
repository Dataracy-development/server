package com.dataracy.modules.project.domain.model.vo;

/**
 * @param topicLabel
 * @param analysisPurposeLabel
 * @param dataSourceLabel
 * @param authorLevelLabel
 */
public record ValidatedProjectInfo(
    String topicLabel,
    String analysisPurposeLabel,
    String dataSourceLabel,
    String authorLevelLabel) {}
