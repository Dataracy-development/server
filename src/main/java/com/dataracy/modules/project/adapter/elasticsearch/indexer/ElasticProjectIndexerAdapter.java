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

    /**
     * 프로젝트를 Soft Delete로 마킹합니다. isDeleted = true 로 부분 업데이트합니다.
     */
    @Override
    public void markAsDeleted(Long projectId) {
        try {
            log.info("프로젝트 soft delete 시작: projectId={}", projectId);
            client.update(u -> u
                            .index(INDEX)
                            .id(projectId.toString())
                            .doc(ProjectDeletedUpdate.deleted()),
                    ProjectSearchDocument.class
            );
            log.info("프로젝트 soft delete 완료: projectId={}", projectId);
        } catch (IOException e) {
            log.error("프로젝트 soft delete 실패: projectId={}", projectId, e);
        }
    }

    @Override
    public void restoreDeleted(Long projectId) {
        try {
            log.info("프로젝트 복원 시작: projectId={}", projectId);
            client.update(u -> u
                            .index(INDEX)
                            .id(projectId.toString())
                            .doc(ProjectDeletedUpdate.restored()),
                    ProjectSearchDocument.class
            );
            log.info("프로젝트 복원 완료: projectId={}", projectId);
        } catch (IOException e) {
            log.error("프로젝트 복원 실패: projectId={}", projectId, e);
        }
    }
}
