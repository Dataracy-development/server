package com.dataracy.modules.dataset.adapter.web.response;

public record CountDataGroupWebResponse(
        Long topicId,
        String topicLabel,
        Long count
) {}
