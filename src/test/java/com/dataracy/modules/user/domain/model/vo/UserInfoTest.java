package com.dataracy.modules.user.domain.model.vo;

import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserInfoTest {

    @Test
    @DisplayName("레코드 생성 및 접근자 검증")
    void record_basic() {
        // given
        UserInfo info = new UserInfo(
                100L,
                RoleType.ROLE_USER,
                "u@test.com",
                "nick",
                1L,
                2L,
                Arrays.asList(10L, 20L),
                3L,
                "p.png",
                "intro"
        );

        // when
        // (레코드 접근)

        // then
        assertThat(info.id()).isEqualTo(100L);
        assertThat(info.role()).isEqualTo(RoleType.ROLE_USER);
        assertThat(info.email()).isEqualTo("u@test.com");
        assertThat(info.nickname()).isEqualTo("nick");
        assertThat(info.authorLevelId()).isEqualTo(1L);
        assertThat(info.occupationId()).isEqualTo(2L);
        assertThat(info.topicIds()).containsExactly(10L, 20L);
        assertThat(info.visitSourceId()).isEqualTo(3L);
        assertThat(info.profileImageUrl()).isEqualTo("p.png");
        assertThat(info.introductionText()).isEqualTo("intro");
    }
}
