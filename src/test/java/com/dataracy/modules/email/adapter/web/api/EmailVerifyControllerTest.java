package com.dataracy.modules.email.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.email.adapter.web.api.validate.EmailVerifyController;
import com.dataracy.modules.email.adapter.web.mapper.validate.ValidateEmailWebMapper;
import com.dataracy.modules.email.adapter.web.request.validate.VerifyCodeWebRequest;
import com.dataracy.modules.email.application.dto.response.GetResetTokenResponse;
import com.dataracy.modules.email.application.port.in.validate.VerifyEmailUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EmailVerifyControllerTest {

    @Spy
    private ValidateEmailWebMapper mapper = new ValidateEmailWebMapper();

    @Mock
    private VerifyEmailUseCase useCase;

    @InjectMocks
    private EmailVerifyController controller;

    @Test
    @DisplayName("성공: 코드 검증 성공 시 200 응답과 resetToken 포함")
    void verifyCodeSuccess() {
        // given
        given(useCase.verifyCode(eq("user@example.com"), eq("123456"), any()))
                .willReturn(new GetResetTokenResponse("jwt-token"));

        VerifyCodeWebRequest req = new VerifyCodeWebRequest(
                "user@example.com",
                "123456",
                "PASSWORD_SEARCH"
        );

        // when
        ResponseEntity<SuccessResponse<GetResetTokenResponse>> res =
                controller.verifyCode(req);

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getData()).isNotNull();
        assertThat(res.getBody().getData().resetToken()).isEqualTo("jwt-token");
    }
}
