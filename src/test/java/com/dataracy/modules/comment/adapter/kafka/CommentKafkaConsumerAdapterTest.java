package com.dataracy.modules.comment.adapter.kafka;

import com.dataracy.modules.comment.adapter.kafka.consumer.CommentKafkaConsumerAdapter;
import com.dataracy.modules.comment.application.port.in.command.count.DecreaseLikeCountUseCase;
import com.dataracy.modules.comment.application.port.in.command.count.IncreaseLikeCountUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

class CommentKafkaConsumerAdapterTest {

    private IncreaseLikeCountUseCase increase;
    private DecreaseLikeCountUseCase decrease;
    private CommentKafkaConsumerAdapter adapter;

    @BeforeEach
    void setup() {
        increase = mock(IncreaseLikeCountUseCase.class);
        decrease = mock(DecreaseLikeCountUseCase.class);
        adapter = new CommentKafkaConsumerAdapter(increase, decrease);
    }

    @Test
    @DisplayName("좋아요 증가 이벤트 처리 성공 → IncreaseLikeCountUseCase 호출 확인")
    void consumeLikeIncreaseSuccess() {
        // when
        adapter.consumeLikeIncrease(1L);

        // then
        then(increase).should().increaseLikeCount(1L);
    }

    @Test
    @DisplayName("좋아요 감소 이벤트 처리 성공 → DecreaseLikeCountUseCase 호출 확인")
    void consumeLikeDecreaseSuccess() {
        // when
        adapter.consumeLikeDecrease(1L);

        // then
        then(decrease).should().decreaseLikeCount(1L);
    }

    @Test
    @DisplayName("좋아요 증가 이벤트 처리 실패 → 예외 그대로 전파")
    void consumeLikeIncreaseFail() {
        // given
        willThrow(new RuntimeException("DB down"))
                .given(increase).increaseLikeCount(1L);

        // when
        RuntimeException ex = catchThrowableOfType(
                () -> adapter.consumeLikeIncrease(1L),
                RuntimeException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("DB down");
    }

    @Test
    @DisplayName("좋아요 감소 이벤트 처리 실패 → 예외 그대로 전파")
    void consumeLikeDecreaseFail() {
        // given
        willThrow(new RuntimeException("Kafka consumer fail"))
                .given(decrease).decreaseLikeCount(1L);

        // when
        RuntimeException ex = catchThrowableOfType(
                () -> adapter.consumeLikeDecrease(1L),
                RuntimeException.class
        );

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("Kafka consumer fail");
    }
}
