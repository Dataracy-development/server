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
     * 인덱싱 과정에서 발생하는 IOException은 로깅만 하며 예외를 외부로 전달하지 않습니다.
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
     * 지정된 데이터셋 문서의 삭제 상태를 업데이트합니다.
     *
     * @param dataId    삭제 상태를 변경할 데이터셋의 ID
     * @param update    적용할 삭제 상태 업데이트 정보
     * @param operation 수행 중인 작업의 설명(예: "soft delete", "복원")
     */
    private void updateDeletedStatus(Long dataId, DataDeletedUpdate update, String operation) {
        try {
            log.info("데이터셋 {} 시작: dataId={}", operation, dataId);
            client.update(u -> u
                            .index(INDEX)
                            .id(dataId.toString())
                            .doc(update),
                    DataSearchDocument.class
            );
            log.info("데이터셋 {} 완료: dataId={}", operation, dataId);
        } catch (IOException e) {
            log.error("데이터셋 {} 실패: dataId={}", operation, dataId, e);
        }
    }

    /**
     * 주어진 데이터 ID에 해당하는 문서를 소프트 삭제 처리합니다.
     *
     * @param dataId 삭제 처리할 데이터의 ID
     */
    @Override
    public void markAsDeleted(Long dataId) {
        updateDeletedStatus(dataId, DataDeletedUpdate.deleted(), "soft delete");
    }

    /**
     * 삭제된 데이터 문서를 복원 상태로 업데이트합니다.
     *
     * @param dataId 복원할 데이터 문서의 ID
     */
    @Override
    public void markAsRestore(Long dataId) {
        updateDeletedStatus(dataId, DataDeletedUpdate.restored(), "복원");
    }
}
