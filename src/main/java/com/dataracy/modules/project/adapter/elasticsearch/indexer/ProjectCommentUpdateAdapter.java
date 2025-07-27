package com.dataracy.modules.project.adapter.elasticsearch.indexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectCommentUpdatePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectCommentUpdateAdapter implements ProjectCommentUpdatePort {

    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";

    @Override
    public void increaseCommentCount(Long projectId) {
        try {
            log.info("Elasticsearch: commentCount 증분 업데이트 시작 - projectId={}", projectId);
            client.update(u -> u
                            .index(INDEX)
                            .id(projectId.toString())
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source("ctx._source.viewCount += 1")
                                    )
                            ),
                    ProjectSearchDocument.class
            );
            log.info("Elasticsearch: commentCount 증분 업데이트 완료 - projectId={}", projectId);
        } catch (IOException e) {
            log.error("Elasticsearch: commentCount 증분 업데이트 실패 - projectId={}", projectId, e);
        }
    }

    @Override
    public void decreaseCommentCount(Long projectId) {
        try {
            log.info("Elasticsearch: commentCount 감분 업데이트 시작 - projectId={}", projectId);
            client.update(u -> u
                            .index(INDEX)
                            .id(projectId.toString())
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source("ctx._source.viewCount -= 1")
                                    )
                            ),
                    ProjectSearchDocument.class
            );
            log.info("Elasticsearch: commentCount 감분 업데이트 완료 - projectId={}", projectId);
        } catch (IOException e) {
            log.error("Elasticsearch: commentCount 감분 업데이트 실패 - projectId={}", projectId, e);
        }
    }
}
