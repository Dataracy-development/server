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
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CheckUserExistsDbAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private CheckUserExistsDbAdapter adapter;

    @Test
    @DisplayName("닉네임 존재 여부 true")
    void existsByNicknameTrue() {
        given(userJpaRepository.existsByNickname("nick")).willReturn(true);

        boolean result = adapter.existsByNickname("nick");

        assertThat(result).isTrue();
        then(userJpaRepository).should().existsByNickname("nick");
    }

    @Test
    @DisplayName("이메일 존재 여부 false")
    void existsByEmailFalse() {
        given(userJpaRepository.existsByEmail("x@test.com")).willReturn(false);

        boolean result = adapter.existsByEmail("x@test.com");

        assertThat(result).isFalse();
        then(userJpaRepository).should().existsByEmail("x@test.com");
    }
}
