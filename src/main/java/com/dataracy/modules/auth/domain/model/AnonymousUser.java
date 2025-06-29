package com.dataracy.modules.auth.domain.model;

import lombok.Getter;

/**
 * 익명 유저 Principal
 */
@Getter
public class AnonymousUser {

    private final String anonymousId;

    private AnonymousUser(String anonymousId) {
        this.anonymousId = anonymousId;
    }

    public static AnonymousUser of(String anonymousId) {
        return new AnonymousUser(anonymousId);
    }
}
