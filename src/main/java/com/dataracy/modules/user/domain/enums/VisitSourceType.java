package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 방문 경로 enum
 */
@Getter
@RequiredArgsConstructor
public enum VisitSourceType {

    SNS("SNS"),
    SEARCH("검색엔진"),
    RECOMMENDATION("지인추천"),
    COMMUNITY("커뮤니티"),
    BLOG("블로그"),
    ADVERTISEMENT("광고"),
    OTHER("기타"),
    ;

    private final String value;

    public static VisitSourceType of(String input) {

        return Arrays.stream(VisitSourceType.values())
                .filter(type -> type.value.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorStatus.INVALID_VISIT_SOURCE_TYPE));
    }
}
