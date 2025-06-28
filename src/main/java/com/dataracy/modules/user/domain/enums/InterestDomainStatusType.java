package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterestDomainStatusType {

    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    AI("인공지능"),
    DATA("데이터분석"),
    SECURITY("보안"),
    DESIGN("디자인"),
    STARTUP("스타트업"),
    ;

    private final String value;

    public static InterestDomainStatusType of(String value) {
        for (InterestDomainStatusType status : InterestDomainStatusType.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new UserException(UserErrorStatus.BAD_REQUEST_DOMAIN_TOPIC_STATUS_TYPE);
    }
}
