package com.dataracy.modules.project.adapter.elasticsearch.indexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectIndexingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProjectIndexerAdapter implements ProjectIndexingPort {

    private final ElasticsearchClient client;

    /**
     * 주어진 프로젝트 검색 문서를 Elasticsearch의 "project" 인덱스에 저장합니다.
     *
     * @param doc 인덱싱할 프로젝트 검색 문서
     */
    @Override
    public void index(ProjectSearchDocument doc) {
        try {
            log.info("프로젝트 인덱싱 시작: projectId={}", doc.id());
            client.index(i -> i
                    .index("project_index")
                    .id(doc.id().toString())
                    .document(doc)
            );
            log.info("프로젝트 인덱싱 완료: projectId={}", doc.id());
        } catch (IOException e) {
            log.error("프로젝트 인덱싱 실패: projectId={}", doc.id(), e);
            // 인덱싱 실패가 프로젝트 업로드 실패를 이끌지 않도록 한다.
//            throw new ProjectException(ProjectErrorStatus.FAIL_INDEXING_PROJECT);
        }
    }
}
