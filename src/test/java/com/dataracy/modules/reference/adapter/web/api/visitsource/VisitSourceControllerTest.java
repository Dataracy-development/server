package com.dataracy.modules.reference.adapter.web.api.visitsource;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.VisitSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllVisitSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.port.in.visitsource.FindAllVisitSourcesUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class VisitSourceControllerTest {

    @Mock FindAllVisitSourcesUseCase findAllVisitSourcesUseCase;
    @Mock VisitSourceWebMapper webMapper;

    @InjectMocks VisitSourceController controller;

    @Test
    @DisplayName("findAllVisitSources API: 성공 - 200 OK와 바디 반환")
    void findAllVisitSources_success() {
        // given
        AllVisitSourcesResponse svc = new AllVisitSourcesResponse(java.util.List.of());
        AllVisitSourcesWebResponse web = new AllVisitSourcesWebResponse(java.util.List.of());
        given(findAllVisitSourcesUseCase.findAllVisitSources()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when
        ResponseEntity<SuccessResponse<AllVisitSourcesWebResponse>> res = controller.findAllVisitSources();

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        then(findAllVisitSourcesUseCase).should().findAllVisitSources();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllVisitSources API: 실패 - 내부 예외 전파")
    void findAllVisitSources_failure_propagates() {
        // given
        given(findAllVisitSourcesUseCase.findAllVisitSources()).willThrow(new RuntimeException("boom"));

        // when
        RuntimeException ex = catchThrowableOfType(() -> controller.findAllVisitSources(), RuntimeException.class);

        // then
        assertThat(ex).isNotNull();
        then(webMapper).shouldHaveNoInteractions();
    }
}
