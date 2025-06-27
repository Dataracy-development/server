package com.dataracy.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum AuthorLevelStatusType {

    BEGINNER("초심자"),
    PRACTITIONER("실무자"),
    EXPERT("전문가"),
    GPT("GPT활용");

    private final String authorLevel;

    public static Optional<AuthorLevelStatusType> of(String authorLevel) {
        return Arrays.stream(AuthorLevelStatusType.values())
                .filter(status -> status.getAuthorLevel().equals(authorLevel))
                .findFirst();
    }
}
