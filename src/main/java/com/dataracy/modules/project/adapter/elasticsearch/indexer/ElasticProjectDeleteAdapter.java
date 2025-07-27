package com.dataracy.modules.project.adapter.elasticsearch.indexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectDeletedUpdate;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectDeletePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticProjectDeleteAdapter implements ProjectDeletePort {

    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";

    /**
     * 프로젝트 문서의 삭제 상태를 Elasticsearch에서 업데이트합니다.
     *
     * @param projectId 삭제 상태를 변경할 프로젝트의 ID
     * @param update 적용할 삭제 상태 업데이트 정보
     * @param operation 수행 중인 작업의 이름(예: "soft delete", "복원")
     */
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

    /**
     * 프로젝트를 소프트 삭제 상태로 표시합니다.
     *
     * @param projectId 삭제할 프로젝트의 ID
     */
    @Override
    public void markAsDeleted(Long projectId) {
        updateDeletedStatus(projectId, ProjectDeletedUpdate.deleted(), "soft delete");
    }

    /**
     * 삭제된 프로젝트를 복원 상태로 표시합니다.
     *
     * @param projectId 복원할 프로젝트의 ID
     */
    @Override
    public void markAsRestore(Long projectId) {
        updateDeletedStatus(projectId, ProjectDeletedUpdate.restored(), "복원");
    }
}
