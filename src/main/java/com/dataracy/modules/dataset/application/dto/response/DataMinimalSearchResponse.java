package com.dataracy.modules.dataset.application.dto.response;

import java.time.LocalDateTime;

public record DataMinimalSearchResponse(
        Long id,
        String title,
        String thumbnailUrl,
        LocalDateTime createAt
) {}
