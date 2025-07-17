package com.dataracy.modules.project.domain.model;

public record ProjectSearchResult(
        Long projectId,
        String title,
        String username
) {}
