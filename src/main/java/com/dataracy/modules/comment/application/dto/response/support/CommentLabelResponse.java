package com.dataracy.modules.comment.application.dto.response.support;

import java.util.Map;

/**
 * 댓글 응답을 위한 보조 DTO (유저 정보 관련)
 *
 * @param usernameMap 작성자 닉네임 map<작성자 아이디, 작성자명>
 * @param userThumbnailMap 작성자 썸네일 map<작성자 아이디, 작성자 썸네일 url>
 * @param userAuthorLevelIds 작성자 유형 아이디 map<작성자 아이디, 작성자 유형 아이디>
 * @param userAuthorLevelLabelMap 작성자 유형 라벨 map<작성자 유형 아이디, 작성자 유형 라벨>
 */
public record CommentLabelResponse(
        Map<Long, String> usernameMap,
        Map<Long, String> userThumbnailMap,
        Map<Long, String> userAuthorLevelIds,
        Map<Long, String> userAuthorLevelLabelMap
) {}
