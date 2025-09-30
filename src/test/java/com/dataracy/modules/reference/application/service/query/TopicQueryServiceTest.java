package com.dataracy.modules.reference.application.service.query;

import com.dataracy.modules.common.logging.ServiceLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;
import com.dataracy.modules.reference.application.mapper.TopicDtoMapper;
import com.dataracy.modules.reference.application.port.out.TopicPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.Topic;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TopicQueryServiceTest {

    @InjectMocks
    private TopicQueryService service;

    @Mock
    private TopicDtoMapper topicDtoMapper;

    @Mock
    private TopicPort topicPort;

    private MockedStatic<LoggerFactory> loggerFactoryMock;
    private ServiceLogger loggerService;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        loggerService = mock(ServiceLogger.class);
        loggerFactoryMock.when(LoggerFactory::service).thenReturn(loggerService);
        doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
        doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
        doNothing().when(loggerService).logWarning(anyString(), anyString());
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("findAllTopics 메서드 테스트")
    class FindAllTopicsTest {

        @Test
        @DisplayName("모든 토픽 조회 성공")
        void findAllTopicsSuccess() {
            // given
            List<Topic> topics = List.of(
                    new Topic(1L, "value1", "토픽 1"),
                    new Topic(2L, "value2", "토픽 2")
            );
            List<TopicResponse> responseList = List.of(
                    new TopicResponse(1L, "value1", "토픽 1"),
                    new TopicResponse(2L, "value2", "토픽 2")
            );
            AllTopicsResponse expectedResponse = new AllTopicsResponse(responseList);

            given(topicPort.findAllTopics()).willReturn(topics);
            given(topicDtoMapper.toResponseDto(topics)).willReturn(expectedResponse);

            // when
            AllTopicsResponse result = service.findAllTopics();

            // then
            assertThat(result).isEqualTo(expectedResponse);
            then(topicPort).should().findAllTopics();
            then(topicDtoMapper).should().toResponseDto(topics);
            then(loggerService).should().logStart(eq("FindAllTopicsUseCase"), 
                    contains("모든 토픽 정보 조회 서비스 시작"));
            then(loggerService).should().logSuccess(eq("FindAllTopicsUseCase"), 
                    contains("모든 토픽 정보 조회 서비스 종료"), any(Instant.class));
        }
    }

    @Nested
    @DisplayName("findTopic 메서드 테스트")
    class FindTopicTest {

        @Test
        @DisplayName("토픽 조회 성공")
        void findTopicSuccess() {
            // given
            Long topicId = 1L;
            Topic topic = new Topic(topicId, "value", "토픽");
            TopicResponse expectedResponse = new TopicResponse(topicId, "value", "토픽");

            given(topicPort.findTopicById(topicId)).willReturn(Optional.of(topic));
            given(topicDtoMapper.toResponseDto(topic)).willReturn(expectedResponse);

            // when
            TopicResponse result = service.findTopic(topicId);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            then(topicPort).should().findTopicById(topicId);
            then(topicDtoMapper).should().toResponseDto(topic);
            then(loggerService).should().logStart(eq("FindTopicUseCase"), 
                    contains("주어진 ID로 토픽 조회 서비스 시작 topicId=" + topicId));
            then(loggerService).should().logSuccess(eq("FindTopicUseCase"), 
                    contains("주어진 ID로 토픽 조회 서비스 종료 topicId=" + topicId), any(Instant.class));
        }

        @Test
        @DisplayName("토픽이 존재하지 않을 때 예외 발생")
        void findTopicFailWhenNotFound() {
            // given
            Long topicId = 999L;
            given(topicPort.findTopicById(topicId)).willReturn(Optional.empty());

            // when & then
            ReferenceException exception = catchThrowableOfType(() -> service.findTopic(topicId), ReferenceException.class);
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode()).isEqualTo(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME);

            then(topicPort).should().findTopicById(topicId);
            then(topicDtoMapper).should(never()).toResponseDto(any(Topic.class));
            then(loggerService).should().logStart(eq("FindTopicUseCase"), 
                    contains("주어진 ID로 토픽 조회 서비스 시작 topicId=" + topicId));
            then(loggerService).should().logWarning(eq("FindTopicUseCase"), 
                    contains("해당 토픽이 존재하지 않습니다. topicId=" + topicId));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }
    }

    @Nested
    @DisplayName("validateTopic 메서드 테스트")
    class ValidateTopicTest {

        @Test
        @DisplayName("토픽 검증 성공")
        void validateTopicSuccess() {
            // given
            Long topicId = 1L;
            given(topicPort.existsTopicById(topicId)).willReturn(true);

            // when
            service.validateTopic(topicId);

            // then
            then(topicPort).should().existsTopicById(topicId);
            then(loggerService).should().logStart(eq("ValidateTopicUseCase"), 
                    contains("주어진 ID에 해당하는 토픽이 존재하는지 확인 서비스 시작 topicId=" + topicId));
            then(loggerService).should().logSuccess(eq("ValidateTopicUseCase"), 
                    contains("주어진 ID에 해당하는 토픽이 존재하는지 확인 서비스 종료 topicId=" + topicId), any(Instant.class));
            then(loggerService).should(never()).logWarning(anyString(), anyString());
        }

        @Test
        @DisplayName("토픽이 존재하지 않을 때 예외 발생")
        void validateTopicFailWhenNotFound() {
            // given
            Long topicId = 999L;
            given(topicPort.existsTopicById(topicId)).willReturn(false);

            // when & then
            ReferenceException exception = catchThrowableOfType(() -> service.validateTopic(topicId), ReferenceException.class);
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode()).isEqualTo(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME);

            then(topicPort).should().existsTopicById(topicId);
            then(loggerService).should().logStart(eq("ValidateTopicUseCase"), 
                    contains("주어진 ID에 해당하는 토픽이 존재하는지 확인 서비스 시작 topicId=" + topicId));
            then(loggerService).should().logWarning(eq("ValidateTopicUseCase"), 
                    contains("해당 토픽이 존재하지 않습니다. topicId=" + topicId));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }
    }

    @Nested
    @DisplayName("getLabelById 메서드 테스트")
    class GetLabelByIdTest {

        @Test
        @DisplayName("라벨 조회 성공")
        void getLabelByIdSuccess() {
            // given
            Long topicId = 1L;
            String expectedLabel = "토픽";
            given(topicPort.getLabelById(topicId)).willReturn(Optional.of(expectedLabel));

            // when
            String result = service.getLabelById(topicId);

            // then
            assertThat(result).isEqualTo(expectedLabel);
            then(topicPort).should().getLabelById(topicId);
            then(loggerService).should().logStart(eq("GetTopicLabelFromIdUseCase"), 
                    contains("주어진 토픽 ID에 해당하는 라벨을 조회 서비스 시작 topicId=" + topicId));
            then(loggerService).should().logSuccess(eq("GetTopicLabelFromIdUseCase"), 
                    contains("주어진 토픽 ID에 해당하는 라벨을 조회 서비스 종료 topicId=" + topicId), any(Instant.class));
        }

        @Test
        @DisplayName("라벨이 존재하지 않을 때 예외 발생")
        void getLabelByIdFailWhenNotFound() {
            // given
            Long topicId = 999L;
            given(topicPort.getLabelById(topicId)).willReturn(Optional.empty());

            // when & then
            ReferenceException exception = catchThrowableOfType(() -> service.getLabelById(topicId), ReferenceException.class);
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode()).isEqualTo(ReferenceErrorStatus.NOT_FOUND_TOPIC_NAME);

            then(topicPort).should().getLabelById(topicId);
            then(loggerService).should().logStart(eq("GetTopicLabelFromIdUseCase"), 
                    contains("주어진 토픽 ID에 해당하는 라벨을 조회 서비스 시작 topicId=" + topicId));
            then(loggerService).should().logWarning(eq("GetTopicLabelFromIdUseCase"), 
                    contains("해당 토픽이 존재하지 않습니다. topicId=" + topicId));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }
    }

    @Nested
    @DisplayName("getLabelsByIds 메서드 테스트")
    class GetLabelsByIdsTest {

        @Test
        @DisplayName("라벨 목록 조회 성공")
        void getLabelsByIdsSuccess() {
            // given
            List<Long> topicIds = List.of(1L, 2L);
            Map<Long, String> expectedLabels = Map.of(
                    1L, "토픽 1",
                    2L, "토픽 2"
            );
            given(topicPort.getLabelsByIds(topicIds)).willReturn(expectedLabels);

            // when
            Map<Long, String> result = service.getLabelsByIds(topicIds);

            // then
            assertThat(result).isEqualTo(expectedLabels);
            then(topicPort).should().getLabelsByIds(topicIds);
            then(loggerService).should().logStart(eq("GetTopicLabelFromIdUseCase"), 
                    contains("토픽 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작"));
            then(loggerService).should().logSuccess(eq("GetTopicLabelFromIdUseCase"), 
                    contains("토픽 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료"), any(Instant.class));
        }

        @Test
        @DisplayName("null ID 목록일 때 빈 Map 반환")
        void getLabelsByIdsWithNullList() {
            // when
            Map<Long, String> result = service.getLabelsByIds(null);

            // then
            assertThat(result).isEmpty();
            then(topicPort).should(never()).getLabelsByIds(any());
            // null/빈 리스트일 때는 logSuccess가 호출되지 않을 수 있음
        }

        @Test
        @DisplayName("빈 ID 목록일 때 빈 Map 반환")
        void getLabelsByIdsWithEmptyList() {
            // when
            Map<Long, String> result = service.getLabelsByIds(List.of());

            // then
            assertThat(result).isEmpty();
            then(topicPort).should(never()).getLabelsByIds(any());
            // null/빈 리스트일 때는 logSuccess가 호출되지 않을 수 있음
        }
    }
}