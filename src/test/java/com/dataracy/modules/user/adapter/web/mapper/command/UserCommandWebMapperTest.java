package com.dataracy.modules.user.adapter.web.mapper.command;

import com.dataracy.modules.user.adapter.web.request.command.ModifyUserInfoWebRequest;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserCommandWebMapperTest {

    private final UserCommandWebMapper mapper = new UserCommandWebMapper();

    @Test
    @DisplayName("toApplicationDto: WebRequest → ApplicationDto 매핑 성공")
    void toApplicationDtoSuccess() {
        // given
        ModifyUserInfoWebRequest webReq = new ModifyUserInfoWebRequest(
                "닉네임",
                2L,
                3L,
                List.of(10L, 20L),
                4L,
                "자기소개"
        );

        // when
        ModifyUserInfoRequest appReq = mapper.toApplicationDto(webReq);

        // then
        assertThat(appReq.nickname()).isEqualTo("닉네임");
        assertThat(appReq.authorLevelId()).isEqualTo(2L);
        assertThat(appReq.occupationId()).isEqualTo(3L);
        assertThat(appReq.topicIds()).containsExactly(10L, 20L);
        assertThat(appReq.visitSourceId()).isEqualTo(4L);
        assertThat(appReq.introductionText()).isEqualTo("자기소개");
    }
}
