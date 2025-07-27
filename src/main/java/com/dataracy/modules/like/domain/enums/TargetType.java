package com.dataracy.modules.like.domain.enums;

import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.status.LikeErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 좋아요 대상 enum
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

