
package com.dataracy.modules.user.adapter.web.mapper.read;

import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserReadWebMapperTest {

    private UserReadWebMapper mapper = new UserReadWebMapper();

    @Test
    @DisplayName("toWebDto: 앱 → 웹 매핑")
    void toWebDto() {
        // given
        GetUserInfoResponse dto = new GetUserInfoResponse(
                1L, RoleType.ROLE_USER, "u@test.com", "nick",
                "author", "job", List.of("A","B"),
                "visit", "img.png", "intro"
        );

        // when
        GetUserInfoWebResponse web = mapper.toWebDto(dto);

        // then
        assertThat(web.id()).isEqualTo(1L);
        assertThat(web.role()).isEqualTo(RoleType.ROLE_USER);
        assertThat(web.email()).isEqualTo("u@test.com");
        assertThat(web.nickname()).isEqualTo("nick");
        assertThat(web.authorLevelLabel()).isEqualTo("author");
        assertThat(web.occupationLabel()).isEqualTo("job");
        assertThat(web.topicLabels()).containsExactly("A","B");
        assertThat(web.visitSourceLabel()).isEqualTo("visit");
        assertThat(web.profileImageUrl()).isEqualTo("img.png");
        assertThat(web.introductionText()).isEqualTo("intro");
    }
}
