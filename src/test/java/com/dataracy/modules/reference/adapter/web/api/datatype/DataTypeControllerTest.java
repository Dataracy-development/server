package com.dataracy.modules.reference.adapter.web.api.datatype;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.DataTypeWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllDataTypesWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataTypesResponse;
import com.dataracy.modules.reference.application.port.in.datatype.FindAllDataTypesUseCase;
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
class DataTypeControllerTest {

    @Mock FindAllDataTypesUseCase findAllDataTypesUseCase;
    @Mock DataTypeWebMapper webMapper;

    @InjectMocks DataTypeController controller;

    @Test
    @DisplayName("findAllDataTypes API: 성공 - 200 OK와 바디 반환")
    void findAllDataTypes_success() {
        // given
        AllDataTypesResponse svc = new AllDataTypesResponse(java.util.List.of());
        AllDataTypesWebResponse web = new AllDataTypesWebResponse(java.util.List.of());
        given(findAllDataTypesUseCase.findAllDataTypes()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when
        ResponseEntity<SuccessResponse<AllDataTypesWebResponse>> res = controller.findAllDataTypes();

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        then(findAllDataTypesUseCase).should().findAllDataTypes();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllDataTypes API: 실패 - 내부 예외 전파")
    void findAllDataTypes_failure_propagates() {
        // given
        given(findAllDataTypesUseCase.findAllDataTypes()).willThrow(new RuntimeException("boom"));

        // when
        RuntimeException ex = catchThrowableOfType(() -> controller.findAllDataTypes(), RuntimeException.class);

        // then
        assertThat(ex).isNotNull();
        then(webMapper).shouldHaveNoInteractions();
    }
}
