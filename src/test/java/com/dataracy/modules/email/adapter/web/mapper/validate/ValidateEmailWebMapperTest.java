package com.dataracy.modules.email.adapter.web.mapper.validate;

import com.dataracy.modules.email.adapter.web.request.validate.VerifyCodeWebRequest;
import com.dataracy.modules.email.adapter.web.response.password.GetResetTokenWebResponse;
import com.dataracy.modules.email.application.dto.request.validate.VerifyCodeRequest;
import com.dataracy.modules.email.application.dto.response.GetResetTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ValidateEmailWebMapperTest {

    @Test
    @DisplayName("웹 DTO -> 앱 DTO 매핑")
    void toApplicationDto() {
        // given
        VerifyCodeWebRequest web = new VerifyCodeWebRequest("user@example.com", "123456", "PASSWORD_RESET");
        ValidateEmailWebMapper mapper = new ValidateEmailWebMapper();

        // when
        VerifyCodeRequest dto = mapper.toApplicationDto(web);

        // then
        assertThat(dto.email()).isEqualTo("user@example.com");
        assertThat(dto.code()).isEqualTo("123456");
        assertThat(dto.purpose()).isEqualTo("PASSWORD_RESET");
    }

    @Test
    @DisplayName("앱 DTO -> 웹 DTO 매핑")
    void toWebDto() {
        // given
        GetResetTokenResponse dto = new GetResetTokenResponse("jwt-token");
        ValidateEmailWebMapper mapper = new ValidateEmailWebMapper();

        // when
        GetResetTokenWebResponse web = mapper.toWebDto(dto);

        // then
        assertThat(web.resetToken()).isEqualTo("jwt-token");
    }
}
