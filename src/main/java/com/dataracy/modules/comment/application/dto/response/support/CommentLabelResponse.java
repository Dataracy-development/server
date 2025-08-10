package com.dataracy.modules.comment.application.dto.response.support;

import java.util.Map;

/**
 *요청
 * @param usernameMap
 * @param userThumbnailMap
 * @param userAuthorLevelIds
 * @param userAuthorLevelLabelMap
 */
public record CommentLabelResponse(
        Map<Long, String> usernameMap,
        Map<Long, String> userThumbnailMap,
        Map<Long, String> userAuthorLevelIds,
        Map<Long, String> userAuthorLevelLabelMap
) {}
