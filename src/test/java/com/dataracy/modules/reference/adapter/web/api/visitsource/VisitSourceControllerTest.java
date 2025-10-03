/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.web.api.visitsource;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import com.dataracy.modules.reference.adapter.web.mapper.VisitSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllVisitSourcesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.VisitSourceWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.port.in.visitsource.FindAllVisitSourcesUseCase;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    controllers = VisitSourceController.class,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              com.dataracy.modules.common.util.CookieUtil.class,
              com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class
            }))
class VisitSourceControllerTest {

  @Autowired private MockMvc mockMvc;

  // ===== MockBean =====
  @MockBean private FindAllVisitSourcesUseCase findAllVisitSourcesUseCase;

  @MockBean private VisitSourceWebMapper webMapper;

  // 공통 모킹 (보안/로그 관련)
  @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
  @MockBean private JwtValidateUseCase jwtValidateUseCase;
  @MockBean private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

  @Test
  @DisplayName("findAllVisitSources API: 성공 - 200 OK와 JSON 응답 검증")
  void findAllVisitSourcesSuccess() throws Exception {
    // given
    AllVisitSourcesResponse svc = new AllVisitSourcesResponse(List.of());
    AllVisitSourcesWebResponse web =
        new AllVisitSourcesWebResponse(List.of(new VisitSourceWebResponse(1L, "GOOGLE", "구글 검색")));

    given(findAllVisitSourcesUseCase.findAllVisitSources()).willReturn(svc);
    given(webMapper.toWebDto(svc)).willReturn(web);

    // when & then
    mockMvc
        .perform(get("/api/v1/references/visit-sources").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.visitSources[0].id").value(1))
        .andExpect(jsonPath("$.data.visitSources[0].value").value("GOOGLE"))
        .andExpect(jsonPath("$.data.visitSources[0].label").value("구글 검색"));

    then(findAllVisitSourcesUseCase).should().findAllVisitSources();
    then(webMapper).should().toWebDto(svc);
  }

  @Test
  @DisplayName("findAllVisitSources API: 실패 - 내부 예외 발생 시 500 반환")
  void findAllVisitSourcesFailure() throws Exception {
    // given
    given(findAllVisitSourcesUseCase.findAllVisitSources()).willThrow(new RuntimeException("boom"));

    // when & then
    mockMvc
        .perform(get("/api/v1/references/visit-sources").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());

    then(webMapper).shouldHaveNoInteractions();
  }
}
