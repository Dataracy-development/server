package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.project.adapter.web.mapper.search.ProjectFilterWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.search.ProjectSearchWebMapper;
import com.dataracy.modules.project.application.port.in.query.search.SearchFilteredProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchRealTimeProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchSimilarProjectsUseCase;
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
@WebMvcTest(controllers = com.dataracy.modules.project.adapter.web.api.search.ProjectSearchController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class ProjectSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectSearchWebMapper projectSearchWebMapper;

    @MockBean
    private ProjectFilterWebMapper projectFilterWebMapper;

    @MockBean
    private SearchRealTimeProjectsUseCase searchRealTimeProjectsUseCase;

    @MockBean
    private SearchSimilarProjectsUseCase searchSimilarProjectsUseCase;

    @MockBean
    private SearchFilteredProjectsUseCase searchFilteredProjectsUseCase;

    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @MockBean
    private SecurityPathConfig securityPathConfig;

    @Test
    @DisplayName("기본 테스트")
    void basicTest() throws Exception {
        // 기본 테스트
        mockMvc.perform(get("/api/v1/test"))
                .andExpect(status().isNotFound());
    }
}
