package com.dataracy.modules.behaviorlog.adapter.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.domain.enums.DeviceType;
import com.dataracy.modules.behaviorlog.domain.enums.LogType;
import com.dataracy.modules.common.support.enums.HttpMethod;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BehaviorLogElasticsearchSaveAdapterTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    private BehaviorLogElasticsearchSaveAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BehaviorLogElasticsearchSaveAdapter(elasticsearchClient);
    }

    @Test
    @DisplayName("행동 로그 저장 성공")
    void saveBehaviorLogSuccess() throws IOException {
        // given
        BehaviorLog behaviorLog = createTestBehaviorLog();
        IndexResponse mockResponse = mock(IndexResponse.class);
        given(mockResponse.id()).willReturn("test-id");
        given(mockResponse.result()).willReturn(mock());
        
        given(elasticsearchClient.index(any(IndexRequest.class))).willReturn(mockResponse);

        // when
        adapter.save(behaviorLog);

        // then
        then(elasticsearchClient).should().index(any(IndexRequest.class));
    }

    @Test
    @DisplayName("Elasticsearch 저장 실패 시 예외를 전파하지 않음")
    void saveBehaviorLogWithIOException() throws IOException {
        // given
        BehaviorLog behaviorLog = createTestBehaviorLog();
        IOException ioException = new IOException("Elasticsearch connection failed");
        
        willThrow(ioException).given(elasticsearchClient).index(any(IndexRequest.class));

        // when
        adapter.save(behaviorLog);

        // then
        then(elasticsearchClient).should().index(any(IndexRequest.class));
        // 예외가 전파되지 않아야 함 (로그만 남김)
    }

    @Test
    @DisplayName("네트워크 타임아웃 예외는 재시도 가능으로 판단")
    void saveBehaviorLogWithTimeoutException() throws IOException {
        // given
        BehaviorLog behaviorLog = createTestBehaviorLog();
        IOException timeoutException = new IOException("timeout occurred");
        
        willThrow(timeoutException).given(elasticsearchClient).index(any(IndexRequest.class));

        // when
        adapter.save(behaviorLog);

        // then
        then(elasticsearchClient).should().index(any(IndexRequest.class));
        // 타임아웃 예외는 재시도 가능으로 처리되어야 함
    }

    private BehaviorLog createTestBehaviorLog() {
        return BehaviorLog.builder()
            .userId("123")
            .action(ActionType.CLICK)
            .deviceType(DeviceType.PC)
            .logType(LogType.ACTION)
            .httpMethod(HttpMethod.GET)
            .timestamp(LocalDateTime.now().toString())
            .build();
    }
}
