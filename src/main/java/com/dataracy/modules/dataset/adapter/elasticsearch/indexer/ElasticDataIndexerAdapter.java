package com.dataracy.modules.dataset.adapter.elasticsearch.indexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataDeletedUpdate;
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

    private static final String INDEX = "data_index";

    /**
     * 주어진 DataSearchDocument를 Elasticsearch의 "data_index" 인덱스에 저장합니다.
     *
     * @param doc 인덱싱할 데이터셋 문서
     */
    @Override
    public void index(DataSearchDocument doc) {
        try {
            log.info("데이터셋 인덱싱 시작: dataId={}", doc.id());
            client.index(i -> i
                    .index(INDEX)
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

    /**
     * 데이터셋을 Soft Delete로 마킹합니다. isDeleted = true 로 부분 업데이트합니다.
     */
    @Override
    public void markAsDeleted(Long dataId) {
        try {
            log.info("데이터셋 soft delete 시작: dataId={}", dataId);
            client.update(u -> u
                            .index(INDEX)
                            .id(dataId.toString())
                            .doc(DataDeletedUpdate.deleted()),
                    DataSearchDocument.class
            );
            log.info("데이터셋 soft delete 완료: dataId={}", dataId);
        } catch (IOException e) {
            log.error("데이터셋 soft delete 실패: dataId={}", dataId, e);
        }
    }

    @Override
    public void markAsRestore(Long dataId) {
        try {
            log.info("데이터셋 복원 시작: dataId={}", dataId);
            client.update(u -> u
                            .index(INDEX)
                            .id(dataId.toString())
                            .doc(DataDeletedUpdate.restored()),
                    DataSearchDocument.class
            );
            log.info("데이터셋 복원 완료: dataId={}", dataId);
        } catch (IOException e) {
            log.error("데이터셋 복원 실패: dataId={}", dataId, e);
        }
    }
}
