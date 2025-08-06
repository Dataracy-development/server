package com.dataracy.modules.like.domain.enums;

import com.dataracy.modules.common.logging.support.LoggerFactory;
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

    /**
     * 주어진 문자열에 해당하는 TargetType enum 값을 반환합니다.
     *
     * 입력값이 TargetType의 value 또는 이름과 대소문자 구분 없이 일치하면 해당 enum 값을 반환합니다.
     * 일치하는 값이 없을 경우 LikeException(INVALID_TARGET_TYPE)을 발생시킵니다.
     *
     * @param input TargetType을 식별할 문자열
     * @return 입력값에 해당하는 TargetType
     * @throws LikeException 유효하지 않은 TargetType 입력 시 발생
     */
    public static TargetType of(String input) {

        return Arrays.stream(TargetType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> {
                    LoggerFactory.domain().logRuleViolation("TargetType", "잘못된 ENUM 타입입니다. PROJECT, COMMENT만 가능합니다.");
                    return new LikeException(LikeErrorStatus.INVALID_TARGET_TYPE);
                });
    }
}
