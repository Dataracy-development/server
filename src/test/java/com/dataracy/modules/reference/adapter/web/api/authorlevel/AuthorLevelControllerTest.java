package com.dataracy.modules.reference.adapter.web.api.authorlevel;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.AuthorLevelWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllAuthorLevelsWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;
import com.dataracy.modules.reference.application.port.in.authorlevel.FindAllAuthorLevelsUseCase;
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
class AuthorLevelControllerTest {

    @Mock FindAllAuthorLevelsUseCase findAllAuthorLevelsUseCase;
    @Mock AuthorLevelWebMapper webMapper;

    @InjectMocks AuthorLevelController controller;

    @Test
    @DisplayName("findAllAuthorLevels API: 성공 - 200 OK와 바디 반환")
    void findAllAuthorLevels_success() {
        // given
        AllAuthorLevelsResponse svc = new AllAuthorLevelsResponse(java.util.List.of());
        AllAuthorLevelsWebResponse web = new AllAuthorLevelsWebResponse(java.util.List.of());
        given(findAllAuthorLevelsUseCase.findAllAuthorLevels()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when
        ResponseEntity<SuccessResponse<AllAuthorLevelsWebResponse>> res = controller.findAllAuthorLevels();

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        then(findAllAuthorLevelsUseCase).should().findAllAuthorLevels();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllAuthorLevels API: 실패 - 내부 예외 전파")
    void findAllAuthorLevels_failure_propagates() {
        // given
        given(findAllAuthorLevelsUseCase.findAllAuthorLevels()).willThrow(new RuntimeException("boom"));

        // when
        RuntimeException ex = catchThrowableOfType(() -> controller.findAllAuthorLevels(), RuntimeException.class);

        // then
        assertThat(ex).isNotNull();
        then(webMapper).shouldHaveNoInteractions();
    }
}
