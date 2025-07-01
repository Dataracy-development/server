package com.dataracy.modules.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorLevelStatusType {

    BEGINNER("초심자"),
    PRACTITIONER("실무자"),
    EXPERT("전문가"),
    GPT("GPT활용");

    private final String value;
}
