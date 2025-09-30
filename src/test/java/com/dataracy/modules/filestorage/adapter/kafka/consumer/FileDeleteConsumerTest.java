package com.dataracy.modules.filestorage.adapter.kafka.consumer;

import com.dataracy.modules.common.logging.KafkaLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class FileDeleteConsumerTest {

    @Mock
    private FileStoragePort fileStoragePort;
    
    @Mock
    private KafkaLogger kafkaLogger;

    private FileDeleteConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new FileDeleteConsumer(fileStoragePort);
        ReflectionTestUtils.setField(consumer, "fileDeletedTopic", "file-delete-topic");
    }

    @Test
    @DisplayName("파일 삭제 이벤트 수신 시 파일 삭제 성공")
    void consumeFileDeleteEventSuccess() {
        // given
        String fileUrl = "http://example.com/file.jpg";
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            consumer.consume(fileUrl);

            // then
            then(fileStoragePort).should().delete(fileUrl);
            then(kafkaLogger).should().logConsume("file-delete-topic", "파일 삭제 이벤트 수신됨: " + fileUrl);
            then(kafkaLogger).should().logConsume("file-delete-topic", "파일 삭제 이벤트 처리 완료: " + fileUrl);
        }
    }

    @Test
    @DisplayName("파일 삭제 이벤트 처리 실패 시 에러 로깅만 하고 예외 재발생하지 않음")
    void consumeFileDeleteEventFailure() {
        // given
        String fileUrl = "http://example.com/file.pdf";
        RuntimeException exception = new RuntimeException("File deletion failed");
        willThrow(exception).given(fileStoragePort).delete(fileUrl);
        
        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(LoggerFactory::kafka).thenReturn(kafkaLogger);

            // when
            consumer.consume(fileUrl);

            // then
            then(fileStoragePort).should().delete(fileUrl);
            then(kafkaLogger).should().logConsume("file-delete-topic", "파일 삭제 이벤트 수신됨: " + fileUrl);
            then(kafkaLogger).should().logError(eq("file-delete-topic"), eq("파일 삭제 이벤트 처리 실패: " + fileUrl), any(RuntimeException.class));
            // 예외가 재발생하지 않아야 함 (DLQ 처리)
        }
    }
}
