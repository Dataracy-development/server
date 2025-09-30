package com.dataracy.modules.reference.adapter.web.api.datatype;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.reference.adapter.web.mapper.DataTypeWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllDataTypesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.DataTypeWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.port.in.datatype.FindAllDataTypesUseCase;
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
@WebMvcTest(controllers = DataTypeController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class DataTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindAllDataTypesUseCase findAllDataTypesUseCase;

    @MockBean
    private DataTypeWebMapper webMapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

    @Test
    @DisplayName("findAllDataTypes API: 성공 - 200 OK와 JSON 응답 검증")
    void findAllDataTypesSuccess() throws Exception {
        // given
        AllDataTypesResponse svc = new AllDataTypesResponse(List.of());
        AllDataTypesWebResponse web = new AllDataTypesWebResponse(
                List.of(
                        new DataTypeWebResponse(1L, "CSV", "Comma Separated Values"),
                        new DataTypeWebResponse(2L, "JSON", "JavaScript Object Notation")
                )
        );

        given(findAllDataTypesUseCase.findAllDataTypes()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when & then
        mockMvc.perform(get("/api/v1/references/data-types")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dataTypes[0].id").value(1))
                .andExpect(jsonPath("$.data.dataTypes[0].value").value("CSV"))
                .andExpect(jsonPath("$.data.dataTypes[0].label").value("Comma Separated Values"))
                .andExpect(jsonPath("$.data.dataTypes[1].id").value(2))
                .andExpect(jsonPath("$.data.dataTypes[1].value").value("JSON"))
                .andExpect(jsonPath("$.data.dataTypes[1].label").value("JavaScript Object Notation"));

        then(findAllDataTypesUseCase).should().findAllDataTypes();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllDataTypes API: 실패 - 내부 예외 발생 시 500 반환")
    void findAllDataTypesFailure() throws Exception {
        // given
        given(findAllDataTypesUseCase.findAllDataTypes()).willThrow(new RuntimeException("boom"));

        // when & then
        mockMvc.perform(get("/api/v1/references/data-types")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        then(webMapper).shouldHaveNoInteractions();
    }
}