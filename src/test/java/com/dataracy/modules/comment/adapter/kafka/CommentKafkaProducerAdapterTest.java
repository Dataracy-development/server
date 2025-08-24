package com.dataracy.modules.comment.adapter.kafka;

import com.dataracy.modules.comment.adapter.kafka.producer.CommentKafkaProducerAdapter;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

class CommentKafkaProducerAdapterTest {

    private KafkaTemplate<String, Long> kafkaTemplate;
    private CommentKafkaProducerAdapter adapter;

    @BeforeEach
    void setup() {
        kafkaTemplate = mock(KafkaTemplate.class);
        adapter = new CommentKafkaProducerAdapter(kafkaTemplate);
    }

    @Test
    @DisplayName("댓글 작성 이벤트 발행 성공 → KafkaTemplate send 호출 확인")
    void sendCommentUploadedEventSuccess() {
        // given
        CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
        future.complete(null); // 정상 완료
        willReturn(future).given(kafkaTemplate).send(any(), any(), any());

        // when
        adapter.sendCommentUploadedEvent(1L);

        // then
        then(kafkaTemplate).should().send(any(), any(), eq(1L));
    }

    @Test
    @DisplayName("댓글 삭제 이벤트 발행 성공 → KafkaTemplate send 호출 확인")
    void sendCommentDeletedEventSuccess() {
        // given
        CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
        future.complete(null); // 정상 완료
        willReturn(future).given(kafkaTemplate).send(any(), any(), any());

        // when
        adapter.sendCommentDeletedEvent(1L);

        // then
        then(kafkaTemplate).should().send(any(), any(), eq(1L));
    }

    @Test
    @DisplayName("댓글 작성 이벤트 발행 실패 → send 시 예외 발생하면 전파")
    void sendCommentUploadedEventFail() {
        // given
        CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka send error"));
        willReturn(future).given(kafkaTemplate).send(any(), any(), any());

        // when
        RuntimeException ex = catchThrowableOfType(
                () -> adapter.sendCommentUploadedEvent(1L),
                RuntimeException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("Kafka send error");
    }

    @Test
    @DisplayName("댓글 삭제 이벤트 발행 실패 → send 시 예외 발생하면 전파")
    void sendCommentDeletedEventFail() {
        // given
        CompletableFuture<SendResult<String, Long>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka send error"));
        willReturn(future).given(kafkaTemplate).send(any(), any(), any());

        // when
        RuntimeException ex = catchThrowableOfType(
                () -> adapter.sendCommentDeletedEvent(1L),
                RuntimeException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("Kafka send error");
    }
}
