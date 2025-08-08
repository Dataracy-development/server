package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDateTime;

public record RecentMinimalDataResponse(
        Long id,
        String title,
        String dataThumbnailUrl,
        LocalDateTime createdAt
) {}
