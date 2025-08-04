package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.out.indexing.IndexProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IndexProjectAdapter implements IndexProjectPort {
    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";

    /**
     * 주어진 프로젝트 검색 문서를 Elasticsearch의 "project_index" 인덱스에 저장하거나 갱신합니다.
     *
     * @param doc 인덱싱 또는 갱신할 프로젝트 검색 문서
     */
    @Override
    public void index(ProjectSearchDocument doc) {
        try {
            client.index(i -> i
                    .index(INDEX)
                    .id(String.valueOf(doc.id()))
                    .document(doc)
            );
            LoggerFactory.elastic().logIndex(INDEX, String.valueOf(doc.id()), "프로젝트 인덱싱 완료");
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 인덱싱 실패: docId=" + doc.id(), e);
        }
    }
}
