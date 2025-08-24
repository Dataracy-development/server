package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SoftDeleteDataEsAdapterTest {

    @Mock
    private ElasticsearchClient client;

    private SoftDeleteDataPort adapter;

    @BeforeEach
    void setUp() {
        adapter = new SoftDeleteDataEsAdapter(client);
    }

    @Test
    void deleteDataShouldUpdateDeletedFlag() throws Exception {
        // given
        given(client.update(
                any(Function.class),
                eq(DataSearchDocument.class)
        )).willReturn(mock(UpdateResponse.class));

        // when
        adapter.deleteData(100L);

        // then
        then(client).should().update(
                any(Function.class),
                eq(DataSearchDocument.class)
        );
    }

    @Test
    void restoreDataShouldUpdateRestoredFlag() throws Exception {
        // given
        given(client.update(
                any(Function.class),
                eq(DataSearchDocument.class)
        )).willReturn(mock(UpdateResponse.class));

        // when
        adapter.restoreData(200L);

        // then
        then(client).should().update(
                any(Function.class),
                eq(DataSearchDocument.class)
        );
    }

    @Test
    void updateShouldThrowEsUpdateExceptionWhenIoError() throws Exception {
        // given
        given(client.update(
                any(Function.class),
                eq(DataSearchDocument.class)
        )).willThrow(new IOException("ES failure"));

        // when
        EsUpdateException thrown = catchThrowableOfType(
                () -> adapter.deleteData(300L),
                EsUpdateException.class
        );

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).contains("ES update failed");
    }
}
