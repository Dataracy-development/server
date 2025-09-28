package com.dataracy.modules.reference.adapter.web.api.occupation;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.reference.adapter.web.mapper.OccupationWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllOccupationsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.OccupationWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.port.in.occupation.FindAllOccupationsUseCase;
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
@WebMvcTest(controllers = OccupationController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class OccupationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindAllOccupationsUseCase findAllOccupationsUseCase;

    @MockBean
    private OccupationWebMapper webMapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

    @Test
    @DisplayName("findAllOccupations API: 성공 - 200 OK와 JSON 응답 검증")
    void findAllOccupationsSuccess() throws Exception {
        // given
        AllOccupationsResponse svc = new AllOccupationsResponse(List.of());
        AllOccupationsWebResponse web = new AllOccupationsWebResponse(
                List.of(new OccupationWebResponse(1L, "DEV", "개발자"))
        );

        given(findAllOccupationsUseCase.findAllOccupations()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when & then
        mockMvc.perform(get("/api/v1/references/occupations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.occupations[0].id").value(1))
                .andExpect(jsonPath("$.data.occupations[0].value").value("DEV"))
                .andExpect(jsonPath("$.data.occupations[0].label").value("개발자"));

        then(findAllOccupationsUseCase).should().findAllOccupations();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllOccupations API: 실패 - 내부 예외 발생 시 500 반환")
    void findAllOccupationsFailure() throws Exception {
        // given
        given(findAllOccupationsUseCase.findAllOccupations()).willThrow(new  RuntimeException("boom"));

        // when & then
        mockMvc.perform(get("/api/v1/references/occupations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        then(webMapper).shouldHaveNoInteractions();
    }
}
