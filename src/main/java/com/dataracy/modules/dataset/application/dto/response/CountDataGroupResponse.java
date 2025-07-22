package com.dataracy.modules.dataset.application.dto.response;

public record CountDataGroupResponse(
        Long topicId,
        String topicLabel,
        int count
) {}
