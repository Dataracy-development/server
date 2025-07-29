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

    /**
     * 지정된 프로젝트의 Elasticsearch 문서에서 commentCount 필드를 1 증가시킵니다.
     *
     * @param projectId commentCount를 증가시킬 프로젝트의 ID
     */
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
                                            .source("ctx._source.commentCount += 1")
                                    )
                            ),
                    ProjectSearchDocument.class
            );
            log.info("Elasticsearch: commentCount 증분 업데이트 완료 - projectId={}", projectId);
        } catch (IOException e) {
            log.error("Elasticsearch: commentCount 증분 업데이트 실패 - projectId={}", projectId, e);
        }
    }

    /**
     * 지정된 프로젝트의 Elasticsearch 문서에서 commentCount 필드를 1 감소시킵니다.
     *
     * @param projectId commentCount를 감소시킬 프로젝트의 ID
     */
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
                                            .source("""
                                            if (ctx._source.commentCount != null && ctx._source.commentCount > 0) {
                                                ctx._source.commentCount -= 1;
                                            } else {
                                                ctx._source.commentCount = 0;
                                            }
                                            """)
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
