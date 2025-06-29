package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.status.UserErrorStatus;
import com.dataracy.modules.user.status.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderStatusType {
    LOCAL("LOCAL"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");

    private final String value;

    public static ProviderStatusType of(String value) {
        for (ProviderStatusType status : ProviderStatusType.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new UserException(UserErrorStatus.BAD_REQUEST_PROVIDER_STATUS_TYPE);
    }
}
