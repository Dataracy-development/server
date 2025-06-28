package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public static VisitSourceStatusType of(String source) {
        for (VisitSourceStatusType status : VisitSourceStatusType.values()) {
            if (status.getSource().equalsIgnoreCase(source)) {
                return status;
            }
        }
        throw new UserException(UserErrorStatus.BAD_REQUEST_VISIT_SOURCE_STATUS_TYPE);
    }
}
