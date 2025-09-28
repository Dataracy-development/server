package com.dataracy.modules.dataset.adapter.kafka.producer;

import com.dataracy.modules.common.logging.KafkaLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class DataKafkaProducerAdapterTest {

    @Mock
    private KafkaTemplate<String, DataUploadEvent> kafkaTemplate;
    
    @Mock
    private KafkaLogger kafkaLogger;

    private DataKafkaProducerAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DataKafkaProducerAdapter(kafkaTemplate);
        ReflectionTestUtils.setField(adapter, "dataUploadTopic", "data-uploaded-topic");
    }

    @Test
    @DisplayName("데이터 업로드 이벤트 발행 성공 시 정상 로깅")
    void sendUploadEventSuccess() {
        // given
        Long dataId = 123L;
        String dataFileUrl = "http://example.com/data.csv";
        String originalFilename = "dataset.csv";
        CompletableFuture<SendResult<String, DataUploadEvent>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        
        willReturn(future).given(kafkaTemplate).send(eq("data-uploaded-topic"), eq("123"), any());
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.sendUploadEvent(dataId, dataFileUrl, originalFilename);

            // then
            then(kafkaTemplate).should().send(eq("data-uploaded-topic"), eq("123"), any());
            then(kafkaLogger).should().logProduce("data-uploaded-topic", "데이터셋 업로드 이벤트 발송됨: dataId=123");
        }
    }

    @Test
    @DisplayName("데이터 업로드 이벤트 발행 실패 시 에러 로깅")
    void sendUploadEventFailure() {
        // given
        Long dataId = 456L;
        String dataFileUrl = "http://example.com/data.xlsx";
        String originalFilename = "dataset.xlsx";
        CompletableFuture<SendResult<String, DataUploadEvent>> future = new CompletableFuture<>();
        RuntimeException exception = new RuntimeException("Kafka send failed");
        future.completeExceptionally(exception);
        
        willReturn(future).given(kafkaTemplate).send(eq("data-uploaded-topic"), eq("456"), any());
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            adapter.sendUploadEvent(dataId, dataFileUrl, originalFilename);

            // then
            then(kafkaTemplate).should().send(eq("data-uploaded-topic"), eq("456"), any());
            then(kafkaLogger).should().logError(eq("data-uploaded-topic"), eq("데이터셋 업로드 이벤트 발송 처리 실패: dataId=456"), any(RuntimeException.class));
        }
    }
}
