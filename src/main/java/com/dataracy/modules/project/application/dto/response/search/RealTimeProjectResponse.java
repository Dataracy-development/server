package com.dataracy.modules.project.application.dto.response.search;

public record RealTimeProjectResponse(
        Long id,
        String title,
        String username,
        String fileUrl
) {}
