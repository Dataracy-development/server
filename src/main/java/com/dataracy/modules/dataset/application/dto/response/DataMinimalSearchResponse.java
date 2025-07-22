package com.dataracy.modules.dataset.application.dto.response;

public record DataMinimalSearchResponse(
        Long id,
        String title,
        String thumbnailUrl
) {}
