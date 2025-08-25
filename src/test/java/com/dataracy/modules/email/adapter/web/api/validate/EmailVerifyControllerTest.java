package com.dataracy.modules.email.adapter.web.api.validate;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.email.adapter.web.mapper.validate.ValidateEmailWebMapper;
import com.dataracy.modules.email.adapter.web.request.validate.VerifyCodeWebRequest;
import com.dataracy.modules.email.application.dto.response.GetResetTokenResponse;
import com.dataracy.modules.email.application.port.in.validate.VerifyEmailUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class EmailVerifyControllerTest {

    @Test
    @DisplayName("코드 검증 성공 시 200 응답과 토큰 포함")
    void verifyCode() {
        // given
        ValidateEmailWebMapper mapper = spy(new ValidateEmailWebMapper());
        VerifyEmailUseCase useCase = mock(VerifyEmailUseCase.class);
        given(useCase.verifyCode(eq("user@example.com"), eq("123456"), any())).willReturn(new GetResetTokenResponse("jwt-token"));
        EmailVerifyController controller = new EmailVerifyController(mapper, useCase);

        // when
        ResponseEntity<SuccessResponse<GetResetTokenResponse>> res = controller.verifyCode(
                new VerifyCodeWebRequest("user@example.com", "123456", "PASSWORD_SEARCH")
        );

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getData()).isNotNull();
        assertThat(res.getBody().getData().resetToken()).isEqualTo("jwt-token");
    }
}
