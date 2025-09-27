package com.dataracy.modules.reference.adapter.web.api.analysispurpose;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.reference.adapter.web.mapper.AnalysisPurposeWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllAnalysisPurposesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.AnalysisPurposeWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.port.in.analysispurpose.FindAllAnalysisPurposesUseCase;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AnalysisPurposeController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.dataracy.modules.common.util.CookieUtil.class, com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class}))
class AnalysisPurposeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FindAllAnalysisPurposesUseCase findAllAnalysisPurposesUseCase;

    @MockBean
    private AnalysisPurposeWebMapper webMapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

    @Test
    @DisplayName("GET /api/v1/references/analysis-purposes - 200 OK & 응답 본문 검증")
    void findAllAnalysisPurposesSuccess() throws Exception {
        // given (서비스 DTO와 웹 DTO를 실제 타입으로 구성)
        AllAnalysisPurposesResponse svc =
                new AllAnalysisPurposesResponse(List.of()); // 내용은 매퍼에서 웹 DTO로 변환되므로 비어도 무방
        AllAnalysisPurposesWebResponse web =
                new AllAnalysisPurposesWebResponse(
                        List.of(
                                new AnalysisPurposeWebResponse(1L, "VALUE_A", "라벨A"),
                                new AnalysisPurposeWebResponse(2L, "VALUE_B", "라벨B")
                        )
                );

        given(findAllAnalysisPurposesUseCase.findAllAnalysisPurposes()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when & then
        mockMvc.perform(get("/api/v1/references/analysis-purposes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 공통 SuccessResponse 포맷을 가정: $.code, $.data 존재 확인
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.data").exists())
                // 리스트와 각 항목 필드 검증 (주신 레코드 구조 유지)
                .andExpect(jsonPath("$.data.analysisPurposes[0].id").value(1))
                .andExpect(jsonPath("$.data.analysisPurposes[0].value").value("VALUE_A"))
                .andExpect(jsonPath("$.data.analysisPurposes[0].label").value("라벨A"))
                .andExpect(jsonPath("$.data.analysisPurposes[1].id").value(2))
                .andExpect(jsonPath("$.data.analysisPurposes[1].value").value("VALUE_B"))
                .andExpect(jsonPath("$.data.analysisPurposes[1].label").value("라벨B"));
    }

    @Test
    @DisplayName("GET /api/v1/references/analysis-purposes - 내부 예외 발생 시 500")
    void findAllAnalysisPurposesFailureReturns500() throws Exception {
        // given
        given(findAllAnalysisPurposesUseCase.findAllAnalysisPurposes())
                .willThrow(new RuntimeException("boom"));

        // when & then
        mockMvc.perform(get("/api/v1/references/analysis-purposes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
