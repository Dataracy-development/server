package com.dataracy.modules.project.adapter.web.response;

public record ProjectRealTimeSearchWebResponse(
        Long id,
        String title,
        String username,
        String fileUrl
) {}
