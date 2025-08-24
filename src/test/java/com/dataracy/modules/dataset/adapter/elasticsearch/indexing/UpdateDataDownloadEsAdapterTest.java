package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateDataDownloadEsAdapterTest {

    @Mock
    private ElasticsearchClient client;

    private UpdateDataDownloadEsAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UpdateDataDownloadEsAdapter(client);
    }

    @Test
    void givenValidId_whenIncreaseDownloadCount_thenShouldSucceed() throws Exception {
        // given
        given(client.update(any(Function.class), eq(DataSearchDocument.class)))
                .willReturn(mock(UpdateResponse.class));

        // when
        adapter.increaseDownloadCount(5L);

        // then
        then(client).should(times(1))
                .update(any(Function.class), eq(DataSearchDocument.class));
    }

    @Test
    void givenIoException_whenIncreaseDownloadCount_thenShouldThrowEsUpdateException() throws Exception {
        // given
        given(client.update(any(Function.class), eq(DataSearchDocument.class)))
                .willThrow(new IOException("fail"));

        // when
        EsUpdateException ex = catchThrowableOfType(
                () -> adapter.increaseDownloadCount(6L),
                EsUpdateException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(client).should(times(1))
                .update(any(Function.class), eq(DataSearchDocument.class));
    }
}
