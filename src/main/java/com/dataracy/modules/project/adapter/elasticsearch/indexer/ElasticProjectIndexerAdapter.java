package com.dataracy.modules.project.adapter.elasticsearch.indexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectDeletedUpdate;
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
    private static final String INDEX = "project_index";

    /**
     * 프로젝트 전체 문서를 새로 인덱싱합니다. 삭제가 아닌 일반 저장/수정용입니다.
     */
    @Override
    public void index(ProjectSearchDocument doc) {
        try {
            log.info("프로젝트 인덱싱 시작: projectId={}", doc.id());
            client.index(i -> i
                    .index(INDEX)
                    .id(doc.id().toString())
                    .document(doc)
            );
            log.info("프로젝트 인덱싱 완료: projectId={}", doc.id());
        } catch (IOException e) {
            log.error("프로젝트 인덱싱 실패: projectId={}", doc.id(), e);
        }
    }

    private void updateDeletedStatus(Long projectId, ProjectDeletedUpdate update, String operation) {
        try {
            log.info("프로젝트 {} 시작: projectId={}", operation, projectId);
            client.update(u -> u
                            .index(INDEX)
                            .id(projectId.toString())
                            .doc(update),
                    ProjectSearchDocument.class
            );
            log.info("프로젝트 {} 완료: projectId={}", operation, projectId);
        } catch (IOException e) {
            log.error("프로젝트 {} 실패: projectId={}", operation, projectId, e);
        }
    }

    @Override
    public void markAsDeleted(Long projectId) {
        updateDeletedStatus(projectId, ProjectDeletedUpdate.deleted(), "soft delete");
    }

    @Override
    public void markAsRestore(Long projectId) {
        updateDeletedStatus(projectId, ProjectDeletedUpdate.restored(), "복원");
    }
}
