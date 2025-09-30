package com.dataracy.modules.reference.adapter.web.api.datasource;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.reference.adapter.web.mapper.DataSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllDataSourcesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.DataSourceWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.port.in.datasource.FindAllDataSourcesUseCase;
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
@WebMvcTest(controllers = DataSourceController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class DataSourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindAllDataSourcesUseCase findAllDataSourcesUseCase;

    @MockBean
    private DataSourceWebMapper webMapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

    @Test
    @DisplayName("findAllDataSources API: 성공 - 200 OK와 JSON 응답 검증")
    void findAllDataSourcesSuccess() throws Exception {
        // given
        AllDataSourcesResponse svc = new AllDataSourcesResponse(List.of());
        AllDataSourcesWebResponse web = new AllDataSourcesWebResponse(
                List.of(
                        new DataSourceWebResponse(1L, "Government", "Government data source"),
                        new DataSourceWebResponse(2L, "Private", "Private data source")
                )
        );

        given(findAllDataSourcesUseCase.findAllDataSources()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when & then
        mockMvc.perform(get("/api/v1/references/data-sources")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dataSources[0].id").value(1))
                .andExpect(jsonPath("$.data.dataSources[0].value").value("Government"))
                .andExpect(jsonPath("$.data.dataSources[0].label").value("Government data source"))
                .andExpect(jsonPath("$.data.dataSources[1].id").value(2))
                .andExpect(jsonPath("$.data.dataSources[1].value").value("Private"))
                .andExpect(jsonPath("$.data.dataSources[1].label").value("Private data source"));

        then(findAllDataSourcesUseCase).should().findAllDataSources();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllDataSources API: 실패 - 내부 예외 발생 시 500 반환")
    void findAllDataSourcesFailure() throws Exception {
        // given
        given(findAllDataSourcesUseCase.findAllDataSources()).willThrow(new RuntimeException("boom"));

        // when & then
        mockMvc.perform(get("/api/v1/references/data-sources")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        then(webMapper).shouldHaveNoInteractions();
    }
}