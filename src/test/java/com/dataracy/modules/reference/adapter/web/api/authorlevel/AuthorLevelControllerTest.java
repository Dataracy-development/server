package com.dataracy.modules.reference.adapter.web.api.authorlevel;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.reference.adapter.web.mapper.AuthorLevelWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllAuthorLevelsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.AuthorLevelWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.port.in.authorlevel.FindAllAuthorLevelsUseCase;
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

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthorLevelController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class AuthorLevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindAllAuthorLevelsUseCase findAllAuthorLevelsUseCase;

    @MockBean
    private AuthorLevelWebMapper webMapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

    @Test
    @DisplayName("findAllAuthorLevels API: 성공 - 200 OK와 응답 JSON 검증")
    void findAllAuthorLevelsSuccess() throws Exception {
        // given
        AllAuthorLevelsResponse svc = new AllAuthorLevelsResponse(List.of());
        AllAuthorLevelsWebResponse web = new AllAuthorLevelsWebResponse(
                List.of(new AuthorLevelWebResponse(1L, "VAL_A", "라벨A"))
        );

        given(findAllAuthorLevelsUseCase.findAllAuthorLevels()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when & then
        mockMvc.perform(get("/api/v1/references/author-levels")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.authorLevels[0].id").value(1))
                .andExpect(jsonPath("$.data.authorLevels[0].value").value("VAL_A"))
                .andExpect(jsonPath("$.data.authorLevels[0].label").value("라벨A"));

        then(findAllAuthorLevelsUseCase).should().findAllAuthorLevels();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllAuthorLevels API: 실패 - 내부 예외 발생 시 500 반환")
    void findAllAuthorLevelsFailure() throws Exception {
        // given
        given(findAllAuthorLevelsUseCase.findAllAuthorLevels()).willThrow(new  RuntimeException("boom"));

        // when & then
        mockMvc.perform(get("/api/v1/references/author-levels")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        then(webMapper).shouldHaveNoInteractions();
    }
}
