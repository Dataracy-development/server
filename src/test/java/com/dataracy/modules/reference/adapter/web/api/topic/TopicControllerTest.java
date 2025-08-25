package com.dataracy.modules.reference.adapter.web.api.topic;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.reference.adapter.web.mapper.TopicWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllTopicsWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.port.in.topic.FindAllTopicsUseCase;
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
class TopicControllerTest {

    @Mock FindAllTopicsUseCase findAllTopicsUseCase;
    @Mock TopicWebMapper webMapper;

    @InjectMocks TopicController controller;

    @Test
    @DisplayName("findAllTopics API: 성공 - 200 OK와 바디 반환")
    void findAllTopics_success() {
        // given
        AllTopicsResponse svc = new AllTopicsResponse(java.util.List.of());
        AllTopicsWebResponse web = new AllTopicsWebResponse(java.util.List.of());
        given(findAllTopicsUseCase.findAllTopics()).willReturn(svc);
        given(webMapper.toWebDto(svc)).willReturn(web);

        // when
        ResponseEntity<SuccessResponse<AllTopicsWebResponse>> res = controller.findAllTopics();

        // then
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).isNotNull();
        then(findAllTopicsUseCase).should().findAllTopics();
        then(webMapper).should().toWebDto(svc);
    }

    @Test
    @DisplayName("findAllTopics API: 실패 - 내부 예외 전파")
    void findAllTopics_failure_propagates() {
        // given
        given(findAllTopicsUseCase.findAllTopics()).willThrow(new RuntimeException("boom"));

        // when
        RuntimeException ex = catchThrowableOfType(() -> controller.findAllTopics(), RuntimeException.class);

        // then
        assertThat(ex).isNotNull();
        then(webMapper).shouldHaveNoInteractions();
    }
}
