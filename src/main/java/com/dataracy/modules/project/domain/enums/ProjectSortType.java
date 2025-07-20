package com.dataracy.modules.project.domain.enums;

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

    public static ProjectSortType of(String input) {

        return Arrays.stream(ProjectSortType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.INVALID_PROJECT_SORT_TYPE));
    }
}
