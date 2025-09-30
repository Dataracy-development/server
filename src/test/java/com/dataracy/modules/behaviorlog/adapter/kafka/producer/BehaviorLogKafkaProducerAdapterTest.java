package com.dataracy.modules.behaviorlog.adapter.kafka.producer;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BehaviorLogKafkaProducerAdapterTest {

    @Mock
    private KafkaTemplate<String, BehaviorLog> kafkaTemplate;

    private BehaviorLogKafkaProducerAdapter producer;

    @BeforeEach
    void setUp() {
        producer = new BehaviorLogKafkaProducerAdapter(kafkaTemplate);
        ReflectionTestUtils.setField(producer, "topic", "behavior-logs");
    }

    @Test
    @DisplayName("userId가 있는 행동 로그 이벤트 발행 성공")
    void sendBehaviorLogWithUserIdSuccess() {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
            .userId("user123")
            .anonymousId("anon456")
            .build();
        
        CompletableFuture<SendResult<String, BehaviorLog>> future = new CompletableFuture<>();
        SendResult<String, BehaviorLog> sendResult = mock(SendResult.class);
        future.complete(sendResult);
        
        willReturn(future).given(kafkaTemplate).send(eq("behavior-logs"), eq("user123"), eq(behaviorLog));

        // when
        producer.send(behaviorLog);

        // then
        then(kafkaTemplate).should().send("behavior-logs", "user123", behaviorLog);
    }

    @Test
    @DisplayName("userId가 없고 anonymousId가 있는 행동 로그 이벤트 발행 성공")
    void sendBehaviorLogWithAnonymousIdSuccess() {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
            .userId(null)
            .anonymousId("anon789")
            .build();
        
        CompletableFuture<SendResult<String, BehaviorLog>> future = new CompletableFuture<>();
        SendResult<String, BehaviorLog> sendResult = mock(SendResult.class);
        future.complete(sendResult);
        
        willReturn(future).given(kafkaTemplate).send(eq("behavior-logs"), eq("anon789"), eq(behaviorLog));

        // when
        producer.send(behaviorLog);

        // then
        then(kafkaTemplate).should().send("behavior-logs", "anon789", behaviorLog);
    }

    @Test
    @DisplayName("userId와 anonymousId가 모두 null인 경우 전송하지 않음")
    void sendBehaviorLogWithNullKeys() {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
            .userId(null)
            .anonymousId(null)
            .build();

        // when
        producer.send(behaviorLog);

        // then
        then(kafkaTemplate).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("행동 로그 이벤트 발행 실패 시 에러 로깅")
    void sendBehaviorLogFailure() {
        // given
        BehaviorLog behaviorLog = BehaviorLog.builder()
            .userId("user999")
            .build();
        
        CompletableFuture<SendResult<String, BehaviorLog>> future = new CompletableFuture<>();
        RuntimeException exception = new RuntimeException("Kafka send failed");
        future.completeExceptionally(exception);
        
        willReturn(future).given(kafkaTemplate).send(eq("behavior-logs"), eq("user999"), eq(behaviorLog));

        // when
        producer.send(behaviorLog);

        // then
        then(kafkaTemplate).should().send("behavior-logs", "user999", behaviorLog);
        // 에러 로깅은 내부적으로 처리됨
    }
}
