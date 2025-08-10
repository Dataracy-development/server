package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDateTime;

/**
 *
 * @param id
 * @param title
 * @param dataThumbnailUrl
 * @param createdAt
 */
public record RecentMinimalDataResponse(
        Long id,
        String title,
        String dataThumbnailUrl,
        LocalDateTime createdAt
) {}
