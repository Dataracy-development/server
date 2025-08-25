package com.dataracy.modules.reference.adapter.web.api.analysispurpose;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.AnalysisPurposeWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllAnalysisPurposesWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.port.in.analysispurpose.FindAllAnalysisPurposesUseCase;
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
class AnalysisPurposeControllerTest {

    @Mock FindAllAnalysisPurposesUseCase findAllAnalysisPurposesUseCase;
    @Mock AnalysisPurposeWebMapper webMapper;

    @InjectMocks AnalysisPurposeController controller;

    @Test
    @DisplayName("findAllAnalysisPurposes API: 성공 - 200 OK와 바디 반환")
    void findAllAnalysisPurposes_success() {
        // given
        AllAnalysisPurposesResponse svc = new AllAnalysisPurposesResponse(java.util.List.of());
        AllAnalysisPurposesWebResponse web = new AllAnalysisPurposesWebResponse(java.util.List.of());
        given(findAllAnalysisPurposesUseCase.findAllAnalysisPurposes()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when
        ResponseEntity<SuccessResponse<AllAnalysisPurposesWebResponse>> res = controller.findAllAnalysisPurposes();

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        then(findAllAnalysisPurposesUseCase).should().findAllAnalysisPurposes();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllAnalysisPurposes API: 실패 - 내부 예외 전파")
    void findAllAnalysisPurposes_failure_propagates() {
        // given
        given(findAllAnalysisPurposesUseCase.findAllAnalysisPurposes()).willThrow(new RuntimeException("boom"));

        // when
        RuntimeException ex = catchThrowableOfType(() -> controller.findAllAnalysisPurposes(), RuntimeException.class);

        // then
        assertThat(ex).isNotNull();
        then(webMapper).shouldHaveNoInteractions();
    }
}
