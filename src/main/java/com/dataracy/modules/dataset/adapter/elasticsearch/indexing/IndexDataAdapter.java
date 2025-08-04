package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.port.out.indexing.IndexDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IndexDataAdapter implements IndexDataPort {
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
            client.index(i -> i
                    .index(INDEX)
                    .id(String.valueOf(doc.id()))
                    .document(doc)
            );
            LoggerFactory.elastic().logIndex(INDEX, String.valueOf(doc.id()), "데이터셋 인덱싱 완료");
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "데이터셋 인덱싱 실패: docId=" + doc.id(), e);
        }
    }
}
