package com.dataracy.modules.dataset.adapter.elasticsearch.indexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataIndexingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticDataIndexerAdapter implements DataIndexingPort {

    private final ElasticsearchClient client;

    @Override
    public void index(DataSearchDocument doc) {
        try {
            log.info("데이터셋 인덱싱 시작: dataId={}", doc.id());
            client.index(i -> i
                    .index("data_index")
                    .id(doc.id().toString())
                    .document(doc)
            );
            log.info("데이터셋 인덱싱 완료: dataId={}", doc.id());
        } catch (IOException e) {
            log.error("데이터셋 인덱싱 실패: dataId={}", doc.id(), e);
            // 인덱싱 실패가 데이터셋 업로드 실패를 이끌지 않도록 한다.
//            throw new DataException(DataErrorStatus.FAIL_INDEXING_DATA);
        }
    }
}
