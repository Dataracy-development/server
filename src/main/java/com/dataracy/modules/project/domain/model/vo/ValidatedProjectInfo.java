package com.dataracy.modules.project.domain.model.vo;

public record ValidatedProjectInfo(
        String topicLabel,
        String analysisPurposeLabel,
        String dataSourceLabel,
        String authorLevelLabel
) {}
