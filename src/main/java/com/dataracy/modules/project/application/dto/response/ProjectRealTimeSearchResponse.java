package com.dataracy.modules.project.application.dto.response;

public record ProjectRealTimeSearchResponse(
        Long id,
        String title,
        String username,
        String fileUrl
) {}
