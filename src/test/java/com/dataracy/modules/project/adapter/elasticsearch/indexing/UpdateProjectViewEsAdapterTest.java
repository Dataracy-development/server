package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UpdateProjectViewEsAdapterTest {

    @Mock ElasticsearchClient client;
    @InjectMocks UpdateProjectViewEsAdapter adapter;

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("increaseViewCount_should_wrap_IOException_as_EsUpdateException")
    void increaseViewCount_should_wrap_IOException_as_EsUpdateException() throws Exception {
        // given
        given(client.update(
                any(Function.class),   // Function<UpdateRequest.Builder<ProjectSearchDocument, ?>, ObjectBuilder<...>>
                eq(ProjectSearchDocument.class)
        )).willThrow(new IOException("es down"));

        // when & then
        assertThatThrownBy(() -> adapter.increaseViewCount(1L, 3L))
                .isInstanceOf(EsUpdateException.class);
    }
}
