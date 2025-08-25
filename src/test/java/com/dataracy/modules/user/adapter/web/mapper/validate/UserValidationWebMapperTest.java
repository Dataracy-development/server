
package com.dataracy.modules.user.adapter.web.mapper.validate;

import com.dataracy.modules.user.adapter.web.request.validate.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validate.DuplicateNicknameRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserValidationWebMapperTest {

    UserValidationWebMapper mapper = new UserValidationWebMapper();

    @Test
    @DisplayName("toApplicationDto: 닉네임 매핑")
    void toApplicationDto() {
        // given
        DuplicateNicknameWebRequest web = new DuplicateNicknameWebRequest("nick");

        // when
        DuplicateNicknameRequest dto = mapper.toApplicationDto(web);

        // then
        assertThat(dto.nickname()).isEqualTo("nick");
    }
}
