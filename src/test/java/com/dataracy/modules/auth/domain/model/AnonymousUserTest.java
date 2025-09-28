package com.dataracy.modules.auth.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AnonymousUser 도메인 모델 테스트
 */
class AnonymousUserTest {

    @Test
    @DisplayName("of() 정적 팩토리 메서드로 AnonymousUser 인스턴스 생성")
    void createAnonymousUserWithOfMethod() {
        // given
        String anonymousId = "anonymous_123";

        // when
        AnonymousUser anonymousUser = AnonymousUser.of(anonymousId);

        // then
        assertThat(anonymousUser).isNotNull();
        assertThat(anonymousUser.getAnonymousId()).isEqualTo(anonymousId);
    }

    @Test
    @DisplayName("빈 문자열로 AnonymousUser 생성")
    void createAnonymousUserWithEmptyString() {
        // given
        String emptyId = "";

        // when
        AnonymousUser anonymousUser = AnonymousUser.of(emptyId);

        // then
        assertThat(anonymousUser).isNotNull();
        assertThat(anonymousUser.getAnonymousId()).isEqualTo(emptyId);
    }

    @Test
    @DisplayName("null로 AnonymousUser 생성")
    void createAnonymousUserWithNull() {
        // given
        String nullId = null;

        // when
        AnonymousUser anonymousUser = AnonymousUser.of(nullId);

        // then
        assertThat(anonymousUser).isNotNull();
        assertThat(anonymousUser.getAnonymousId()).isNull();
    }

    @Test
    @DisplayName("긴 문자열로 AnonymousUser 생성")
    void createAnonymousUserWithLongString() {
        // given
        String longId = "a".repeat(1000);

        // when
        AnonymousUser anonymousUser = AnonymousUser.of(longId);

        // then
        assertThat(anonymousUser).isNotNull();
        assertThat(anonymousUser.getAnonymousId()).isEqualTo(longId);
    }

    @Test
    @DisplayName("특수문자가 포함된 AnonymousUser 생성")
    void createAnonymousUserWithSpecialCharacters() {
        // given
        String specialId = "anonymous@#$%^&*()_+{}|:<>?[]\\;'\",./";

        // when
        AnonymousUser anonymousUser = AnonymousUser.of(specialId);

        // then
        assertThat(anonymousUser).isNotNull();
        assertThat(anonymousUser.getAnonymousId()).isEqualTo(specialId);
    }
}
