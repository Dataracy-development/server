package com.dataracy.modules.dataset.adapter.web.response;

import java.time.LocalDateTime;

public record DataMinimalSearchWebResponse(
        Long id,
        String title,
        String thumbnailUrl,
        LocalDateTime createAt
) {}
