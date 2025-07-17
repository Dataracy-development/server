package com.dataracy.modules.project.adapter.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.project.adapter.index.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.out.ProjectIndexingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ElasticProjectIndexerAdapter implements ProjectIndexingPort {

    private final ElasticsearchClient client;

    @Override
    public void index(ProjectSearchDocument doc) {
        try {
            client.index(i -> i
                    .index("project")
                    .id(doc.id().toString())
                    .document(doc)
            );
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 색인 실패", e);
        }
    }
}
