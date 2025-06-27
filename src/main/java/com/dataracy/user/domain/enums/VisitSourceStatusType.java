package com.dataracy.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum VisitSourceStatusType {

    SNS("SNS"),
    SEARCH("검색엔진"),
    RECOMMENDATION("지인추천"),
    COMMUNITY("커뮤니티"),
    BLOG("블로그"),
    ADVERTISEMENT("광고"),
    OTHER("기타"),
    ;

    private final String source;

    public static Optional<VisitSourceStatusType> of(String source) {
        return Arrays.stream(VisitSourceStatusType.values())
                .filter(status -> status.getSource().equals(source))
                .findFirst();
    }
}
