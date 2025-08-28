package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import com.dataracy.modules.reference.application.mapper.TopicDtoMapper;
import com.dataracy.modules.reference.application.port.out.TopicPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TopicQueryServiceTest {

    @Mock
    private TopicPort topicPort;

    @Mock
    private TopicDtoMapper topicDtoMapper;

    @InjectMocks
    private TopicQueryService service;

    @Test
    @DisplayName("전체 토픽 조회 성공")
    void findAllTopicsSuccess() {
        // given
        List<Topic> domainList = List.of(
                new Topic(1L, "v1", "l1"),
                new Topic(2L, "v2", "l2")
        );
        AllTopicsResponse mapped = new AllTopicsResponse(
                List.of(
                        new TopicResponse(1L, "v1", "l1"),
                        new TopicResponse(2L, "v2", "l2")
                )
        );
        given(topicPort.findAllTopics()).willReturn(domainList);
        given(topicDtoMapper.toResponseDto(domainList)).willReturn(mapped);

        // when
        AllTopicsResponse result = service.findAllTopics();

        // then
        assertThat(result).isSameAs(mapped);
        then(topicPort).should().findAllTopics();
        then(topicDtoMapper).should().toResponseDto(domainList);
    }

    @Test
    @DisplayName("토픽 단건 조회 성공")
    void findTopicSuccess() {
        // given
        Long id = 10L;
        Topic domain = new Topic(id, "v", "l");
        TopicResponse mapped = new TopicResponse(id, "v", "l");
        given(topicPort.findTopicById(id)).willReturn(Optional.of(domain));
        given(topicDtoMapper.toResponseDto(domain)).willReturn(mapped);

        // when
        TopicResponse result = service.findTopic(id);

        // then
        assertThat(result).isSameAs(mapped);
        then(topicPort).should().findTopicById(id);
        then(topicDtoMapper).should().toResponseDto(domain);
    }

    @Test
    @DisplayName("토픽 단건 조회 실패 - 없을 때 예외 발생")
    void findTopicFailWhenNotFound() {
        // given
        Long id = 999L;
        given(topicPort.findTopicById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.findTopic(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(topicPort).should().findTopicById(id);
        then(topicDtoMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("토픽 라벨 조회 성공")
    void getLabelByIdSuccess() {
        // given
        Long id = 1L;
        given(topicPort.getLabelById(id)).willReturn(Optional.of("label"));

        // when
        String label = service.getLabelById(id);

        // then
        assertThat(label).isEqualTo("label");
        then(topicPort).should().getLabelById(id);
    }

    @Test
    @DisplayName("토픽 라벨 조회 실패 - 없을 때 예외 발생")
    void getLabelByIdFailWhenNotFound() {
        // given
        Long id = 404L;
        given(topicPort.getLabelById(id)).willReturn(Optional.empty());

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.getLabelById(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(topicPort).should().getLabelById(id);
    }

    @Test
    @DisplayName("토픽 검증 성공 - 존재할 때")
    void validateTopicSuccess() {
        // given
        Long id = 1L;
        given(topicPort.existsTopicById(id)).willReturn(true);

        // when
        service.validateTopic(id);

        // then
        then(topicPort).should().existsTopicById(id);
    }

    @Test
    @DisplayName("토픽 검증 실패 - 없을 때 예외 발생")
    void validateTopicFailWhenNotFound() {
        // given
        Long id = 2L;
        given(topicPort.existsTopicById(id)).willReturn(false);

        // when
        ReferenceException ex = catchThrowableOfType(
                () -> service.validateTopic(id),
                ReferenceException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(topicPort).should().existsTopicById(id);
    }

    @Test
    @DisplayName("토픽 라벨 다건 조회 성공 및 빈 값 처리")
    void getLabelsByIdsSuccessAndEmptyHandling() {
        // given - empty/null
        assertThat(service.getLabelsByIds(null)).isEmpty();
        assertThat(service.getLabelsByIds(List.of())).isEmpty();

        // given - values
        List<Long> ids = List.of(1L, 2L);
        given(topicPort.getLabelsByIds(ids)).willReturn(Map.of(1L, "L1", 2L, "L2"));

        // when
        Map<Long, String> result = service.getLabelsByIds(ids);

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(topicPort).should().getLabelsByIds(ids);
    }
}
