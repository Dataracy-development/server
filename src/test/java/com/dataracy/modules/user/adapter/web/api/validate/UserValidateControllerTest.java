package com.dataracy.modules.user.adapter.web.api.validate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.user.adapter.web.mapper.validate.UserValidationWebMapper;
import com.dataracy.modules.user.adapter.web.request.validate.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validate.DuplicateNicknameRequest;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    controllers = UserValidateController.class,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              com.dataracy.modules.common.util.CookieUtil.class,
              com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class
            }))
class UserValidateControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserValidationWebMapper userWebMapper;

  @MockBean private DuplicateNicknameUseCase duplicateNicknameUseCase;

  @MockBean private JwtValidateUseCase jwtValidateUseCase;

  @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

  @MockBean private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

  @Test
  @DisplayName("duplicateNickname API: 성공 → 200 OK + 코드 검증")
  void duplicateNicknameSuccess() throws Exception {
    // given
    DuplicateNicknameWebRequest webReq = new DuplicateNicknameWebRequest("nick");
    DuplicateNicknameRequest reqDto = new DuplicateNicknameRequest("nick");

    given(userWebMapper.toApplicationDto(any(DuplicateNicknameWebRequest.class)))
        .willReturn(reqDto);
    willDoNothing().given(duplicateNicknameUseCase).validateDuplicatedNickname("nick");

    // when & then
    mockMvc
        .perform(
            post("/api/v1/nickname/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webReq)))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code").value(UserSuccessStatus.OK_NOT_DUPLICATED_NICKNAME.getCode()));

    then(userWebMapper).should().toApplicationDto(any(DuplicateNicknameWebRequest.class));
    then(duplicateNicknameUseCase).should().validateDuplicatedNickname("nick");
  }

  @Test
  @DisplayName("duplicateNickname API: 실패 → 중복 닉네임 → 409 Conflict + 에러 메시지")
  void duplicateNicknameFailureConflict() throws Exception {
    // given
    DuplicateNicknameWebRequest webReq = new DuplicateNicknameWebRequest("dup");
    DuplicateNicknameRequest reqDto = new DuplicateNicknameRequest("dup");

    given(userWebMapper.toApplicationDto(any(DuplicateNicknameWebRequest.class)))
        .willReturn(reqDto);
    willThrow(new UserException(UserErrorStatus.DUPLICATED_NICKNAME))
        .given(duplicateNicknameUseCase)
        .validateDuplicatedNickname("dup");

    // when & then
    mockMvc
        .perform(
            post("/api/v1/nickname/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webReq)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value(UserErrorStatus.DUPLICATED_NICKNAME.getCode()))
        .andExpect(jsonPath("$.message").value(UserErrorStatus.DUPLICATED_NICKNAME.getMessage()));

    then(userWebMapper).should().toApplicationDto(any(DuplicateNicknameWebRequest.class));
    then(duplicateNicknameUseCase).should().validateDuplicatedNickname("dup");
  }

  @Test
  @DisplayName("duplicateNickname API: 실패 → nickname=null → 400 Bad Request + 검증 메시지")
  void duplicateNicknameFailureValidation() throws Exception {
    // given
    DuplicateNicknameWebRequest webReq = new DuplicateNicknameWebRequest(null);

    // when & then
    mockMvc
        .perform(
            post("/api/v1/nickname/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webReq)))
        .andExpect(status().isBadRequest());

    // 컨트롤러 진입 전 → mock은 호출되지 않음
    then(userWebMapper).shouldHaveNoInteractions();
    then(duplicateNicknameUseCase).shouldHaveNoInteractions();
  }
}
