package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("updateDataDownloadEsAdapter")
@RequiredArgsConstructor
public class UpdateDataDownloadEsAdapter implements UpdateDataDownloadPort {

    private final ElasticsearchClient client;
    private static final String INDEX = "data_index";
    private static final String INCREASE_DOWNLOAD_SCRIPT = """
            if (ctx._source.downloadCount == null) {
                ctx._source.downloadCount = 1;
            } else {
                ctx._source.downloadCount += 1;
            }
            """;

    @Override
    public void increaseDownloadCount(Long dataId) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(String.valueOf(dataId))
                            .script(s -> s.inline(i -> i
                                    .lang("painless")
                                    .source(INCREASE_DOWNLOAD_SCRIPT)))
                            .upsert(DataSearchDocument.builder()
                                    .id(dataId)
                                    .downloadCount(1)
                                    .isDeleted(false)
                                    .build()),
                    DataSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(dataId), "dataset download++ 완료");
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "dataset download++ 실패 dataId=" + dataId, e);
            throw new EsUpdateException("ES update failed: dataId=" + dataId, e);
        }
    }
}
