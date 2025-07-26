package com.dataracy.modules.like.domain.enums;

import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 프로젝트 정렬 enum
 */
@Getter
@RequiredArgsConstructor
public enum TargetType {
    PROJECT("PROJECT"),
    COMMENT("COMMENT"),
    ;

    private final String value;

    public static TargetType of(String input) {

        return Arrays.stream(TargetType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new LikeException(LikeErrorStatus.INVALID_TARGET_TYPE));
    }
}

