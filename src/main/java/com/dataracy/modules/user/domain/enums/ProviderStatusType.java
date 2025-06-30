package com.dataracy.modules.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderStatusType {
    LOCAL("LOCAL"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");

    private final String value;
}
