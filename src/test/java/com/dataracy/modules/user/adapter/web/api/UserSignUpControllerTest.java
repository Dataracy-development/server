package com.dataracy.modules.user.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.security.config.SecurityPathConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = com.dataracy.modules.user.adapter.web.api.signup.UserSignUpController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class UserSignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @MockBean
    private SecurityPathConfig securityPathConfig;

    @MockBean
    private com.dataracy.modules.user.adapter.web.mapper.signup.UserSignUpWebMapper userSignUpWebMapper;

    @MockBean
    private com.dataracy.modules.user.application.port.in.command.signup.SelfSignUpUseCase selfSignUpUseCase;

    @MockBean
    private com.dataracy.modules.user.application.port.in.command.signup.OAuthSignUpUseCase oauthSignUpUseCase;

    @MockBean
    private com.dataracy.modules.common.util.CookieUtil cookieUtil;

    @Test
    @DisplayName("기본 테스트")
    void basicTest() throws Exception {
        // 기본 테스트
        mockMvc.perform(get("/api/v1/test"))
                .andExpect(status().isNotFound());
    }
}
