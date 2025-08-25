package com.dataracy.modules.reference.adapter.web.api.occupation;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.OccupationWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllOccupationsWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllOccupationsResponse;
import com.dataracy.modules.reference.application.port.in.occupation.FindAllOccupationsUseCase;
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
class OccupationControllerTest {

    @Mock FindAllOccupationsUseCase findAllOccupationsUseCase;
    @Mock OccupationWebMapper webMapper;

    @InjectMocks OccupationController controller;

    @Test
    @DisplayName("findAllOccupations API: 성공 - 200 OK와 바디 반환")
    void findAllOccupations_success() {
        // given
        AllOccupationsResponse svc = new AllOccupationsResponse(java.util.List.of());
        AllOccupationsWebResponse web = new AllOccupationsWebResponse(java.util.List.of());
        given(findAllOccupationsUseCase.findAllOccupations()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when
        ResponseEntity<SuccessResponse<AllOccupationsWebResponse>> res = controller.findAllOccupations();

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        then(findAllOccupationsUseCase).should().findAllOccupations();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllOccupations API: 실패 - 내부 예외 전파")
    void findAllOccupations_failure_propagates() {
        // given
        given(findAllOccupationsUseCase.findAllOccupations()).willThrow(new RuntimeException("boom"));

        // when
        RuntimeException ex = catchThrowableOfType(() -> controller.findAllOccupations(), RuntimeException.class);

        // then
        assertThat(ex).isNotNull();
        then(webMapper).shouldHaveNoInteractions();
    }
}
