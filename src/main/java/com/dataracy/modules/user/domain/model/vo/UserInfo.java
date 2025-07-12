package com.dataracy.modules.user.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;

import java.util.List;

/**
 * 외부 도메인에 공개할 수 있는 유저 정보 vo
 */
public record UserInfo(
        Long id,
        RoleType role,
        String email,
        String nickname,
        Long authorLevelId,
        Long occupationId,
        List<Long> topicIds,
        Long visitSourceId
) {}
