
package com.dataracy.modules.user.adapter.web.api.validate;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.mapper.validate.UserValidationWebMapper;
import com.dataracy.modules.user.adapter.web.request.validate.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validate.DuplicateNicknameRequest;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidateControllerTest {

    @Mock UserValidationWebMapper userWebMapper;
    @Mock DuplicateNicknameUseCase duplicateNicknameUseCase;

    @InjectMocks UserValidateController controller;

    @Test
    @DisplayName("duplicateNickname: 정상 플로우 → 200 OK")
    void duplicateNickname_success() {
        // given
        DuplicateNicknameWebRequest webRequest = new DuplicateNicknameWebRequest("nick"); // ← null 대신 실제 객체
        DuplicateNicknameRequest reqDto = new DuplicateNicknameRequest("nick");

        given(userWebMapper.toApplicationDto(any(DuplicateNicknameWebRequest.class)))
                .willReturn(reqDto);

        // when
        ResponseEntity<SuccessResponse<Void>> res = controller.duplicateNickname(webRequest);

        // then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(userWebMapper).should().toApplicationDto(any(DuplicateNicknameWebRequest.class));
        then(duplicateNicknameUseCase).should().validateDuplicatedNickname("nick");
    }


    @Test
    @DisplayName("duplicateNickname: 유즈케이스 예외 → 전파")
    void duplicateNickname_fail_propagates() {
        // given
        DuplicateNicknameWebRequest webRequest = new DuplicateNicknameWebRequest("dup");
        DuplicateNicknameRequest reqDto = new DuplicateNicknameRequest("dup");

        given(userWebMapper.toApplicationDto(any(DuplicateNicknameWebRequest.class)))
                .willReturn(reqDto);
        willThrow(new RuntimeException("dup"))
                .given(duplicateNicknameUseCase).validateDuplicatedNickname("dup");

        // when
        Throwable ex = catchThrowable(() -> controller.duplicateNickname(webRequest));

        // then
        assertThat(ex).isInstanceOf(RuntimeException.class).hasMessageContaining("dup");
    }

}
