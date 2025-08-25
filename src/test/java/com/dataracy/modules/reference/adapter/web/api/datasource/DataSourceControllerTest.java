package com.dataracy.modules.reference.adapter.web.api.datasource;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.DataSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllDataSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.port.in.datasource.FindAllDataSourcesUseCase;
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
class DataSourceControllerTest {

    @Mock FindAllDataSourcesUseCase findAllDataSourcesUseCase;
    @Mock DataSourceWebMapper webMapper;

    @InjectMocks DataSourceController controller;

    @Test
    @DisplayName("findAllDataSources API: 성공 - 200 OK와 바디 반환")
    void findAllDataSources_success() {
        // given
        AllDataSourcesResponse svc = new AllDataSourcesResponse(java.util.List.of());
        AllDataSourcesWebResponse web = new AllDataSourcesWebResponse(java.util.List.of());
        given(findAllDataSourcesUseCase.findAllDataSources()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when
        ResponseEntity<SuccessResponse<AllDataSourcesWebResponse>> res = controller.findAllDataSources();

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        then(findAllDataSourcesUseCase).should().findAllDataSources();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllDataSources API: 실패 - 내부 예외 전파")
    void findAllDataSources_failure_propagates() {
        // given
        given(findAllDataSourcesUseCase.findAllDataSources()).willThrow(new RuntimeException("boom"));

        // when
        RuntimeException ex = catchThrowableOfType(() -> controller.findAllDataSources(), RuntimeException.class);

        // then
        assertThat(ex).isNotNull();
        then(webMapper).shouldHaveNoInteractions();
    }
}
