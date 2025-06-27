package com.dataracy.modules.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ProviderStatusType {
    LOCAL("LOCAL"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");

    private final String providerName;

    public static Optional<ProviderStatusType> of(String providerName) {
        return Arrays.stream(ProviderStatusType.values())
                .filter(status -> status.getProviderName().equalsIgnoreCase(providerName))
                .findFirst();
    }
}
