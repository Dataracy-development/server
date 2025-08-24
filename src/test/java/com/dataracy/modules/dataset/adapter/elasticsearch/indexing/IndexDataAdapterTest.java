package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.port.out.indexing.IndexDataPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class IndexDataAdapterTest {

    @Mock
    private ElasticsearchClient client;

    private IndexDataPort adapter; // 포트 타입으로 선언

    @BeforeEach
    void setUp() {
        adapter = new IndexDataAdapter(client); // 구현체 주입
    }

    @Test
    void givenValidDocument_whenIndex_thenShouldSucceed() throws Exception {
        // given
        DataSearchDocument doc = DataSearchDocument.builder()
                .id(1L)
                .title("test")
                .build();

        given(client.index(any(Function.class)))
                .willReturn(mock(IndexResponse.class));

        // when
        adapter.index(doc);

        // then
        then(client).should(times(1)).index(any(Function.class));
    }

    @Test
    void givenIOException_whenIndex_thenShouldCatchAndNotThrow() throws Exception {
        // given
        DataSearchDocument doc = DataSearchDocument.builder()
                .id(1L)
                .title("test")
                .build();

        given(client.index(any(Function.class)))
                .willThrow(new IOException("fail"));

        // when
        Throwable ex = catchThrowableOfType(() -> adapter.index(doc), Exception.class);

        // then
        assertThat(ex).isNull();
        then(client).should(times(1)).index(any(Function.class));
    }
}
