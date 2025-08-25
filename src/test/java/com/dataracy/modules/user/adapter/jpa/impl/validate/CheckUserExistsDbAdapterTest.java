package com.dataracy.modules.user.adapter.jpa.impl.validate;

import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CheckUserExistsDbAdapterTest {

    @Mock UserJpaRepository userJpaRepository;
    @InjectMocks CheckUserExistsDbAdapter adapter;

    @Test
    @DisplayName("existsByNickname: true 반환")
    void existsByNickname_true() {
        given(userJpaRepository.existsByNickname("nick")).willReturn(true);

        boolean result = adapter.existsByNickname("nick");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByEmail: false 반환")
    void existsByEmail_false() {
        given(userJpaRepository.existsByEmail("x@test.com")).willReturn(false);

        boolean result = adapter.existsByEmail("x@test.com");

        assertThat(result).isFalse();
    }
}
