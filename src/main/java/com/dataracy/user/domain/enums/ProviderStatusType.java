package com.dataracy.user.domain.enums;

import com.dataracy.user.status.UserErrorStatus;
import com.dataracy.user.status.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderStatusType {
    LOCAL("LOCAL"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");

    private final String providerName;

    public static ProviderStatusType of(String providerName) {
        for (ProviderStatusType status : ProviderStatusType.values()){
            if (status.getProviderName().equalsIgnoreCase(providerName)){
                return status;
            }
        }
        throw new UserException(UserErrorStatus.BAD_REQUEST_PROVIDER_STATUS_TYPE);
    }
}
