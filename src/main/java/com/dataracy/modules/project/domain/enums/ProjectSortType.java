package com.dataracy.modules.project.domain.enums;

import com.dataracy.modules.common.logging.support.LoggerFactory;
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
public enum ProjectSortType {
    LATEST("LATEST"),
    OLDEST("OLDEST"),
    MOST_LIKED("MOST_LIKED"),
    MOST_VIEWED("MOST_VIEWED"),
    MOST_COMMENTED("MOST_COMMENTED"),
    LEAST_COMMENTED("LEAST_COMMENTED"),
    ;

    private final String value;

    /**
     * 주어진 문자열 입력값에 해당하는 ProjectSortType 열거형 상수를 반환합니다.
     *
     * 입력값은 열거형 상수의 이름 또는 value 필드와 대소문자 구분 없이 매칭됩니다.
     * 일치하는 정렬 타입이 없을 경우 ProjectException이 발생합니다.
     *
     * @param input 프로젝트 정렬 타입을 나타내는 문자열
     * @return 매칭되는 ProjectSortType 열거형 상수
     * @throws ProjectException 유효하지 않은 정렬 타입 입력 시 발생
     */
    public static ProjectSortType of(String input) {

        return Arrays.stream(ProjectSortType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> {
                    LoggerFactory.domain().logRuleViolation("ProjectSortType", "잘못된 ENUM 타입입니다. LATEST, OLDEST, MOST_LIKED, MOST_VIEWED, MOST_COMMENTED, LEAST_COMMENTED만 가능합니다.");
                    return new ProjectException(ProjectErrorStatus.INVALID_PROJECT_SORT_TYPE);
                });
    }
}
