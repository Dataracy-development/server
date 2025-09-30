package com.dataracy.modules.reference.adapter.web.api.topic;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.reference.adapter.web.mapper.TopicWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllTopicsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.TopicWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import com.dataracy.modules.reference.application.port.in.topic.FindAllTopicsUseCase;
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
@WebMvcTest(controllers = TopicController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindAllTopicsUseCase findAllTopicsUseCase;

    @MockBean
    private TopicWebMapper webMapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

    @Test
    @DisplayName("findAllTopics API: 성공 - 200 OK와 JSON 응답 검증")
    void findAllTopicsSuccess() throws Exception {
        // given
        AllTopicsResponse svc = new AllTopicsResponse(List.of());
        AllTopicsWebResponse web = new AllTopicsWebResponse(
                List.of(
                        new TopicWebResponse(1L, "AI", "인공지능"),
                        new TopicWebResponse(2L, "DATA", "데이터 분석")
                )
        );

        given(findAllTopicsUseCase.findAllTopics()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when & then
        mockMvc.perform(get("/api/v1/references/topics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.topics[0].id").value(1))
                .andExpect(jsonPath("$.data.topics[0].value").value("AI"))
                .andExpect(jsonPath("$.data.topics[0].label").value("인공지능"))
                .andExpect(jsonPath("$.data.topics[1].id").value(2))
                .andExpect(jsonPath("$.data.topics[1].value").value("DATA"))
                .andExpect(jsonPath("$.data.topics[1].label").value("데이터 분석"));

        then(findAllTopicsUseCase).should().findAllTopics();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllTopics API: 실패 - 내부 예외 발생 시 500 반환")
    void findAllTopicsFailure() throws Exception {
        // given
        given(findAllTopicsUseCase.findAllTopics()).willThrow(new RuntimeException("boom"));

        // when & then
        mockMvc.perform(get("/api/v1/references/topics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        then(webMapper).shouldHaveNoInteractions();
    }
}