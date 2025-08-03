package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectDeletedUpdate;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.out.command.delete.SoftDeleteProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("softDeleteProjectEsAdapter")
@RequiredArgsConstructor
public class SoftDeleteProjectEsAdapter implements SoftDeleteProjectPort {
    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";

    /**
     * 프로젝트의 삭제 상태를 Elasticsearch 인덱스에서 업데이트합니다.
     *
     * @param projectId 삭제 상태를 변경할 프로젝트의 ID
     * @param update 적용할 삭제 상태 정보
     * @param operation 수행 작업의 이름(예: "soft delete", "복원")
     */
    private void updateDeletedStatus(Long projectId, ProjectDeletedUpdate update, String operation) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(String.valueOf(projectId))
                            .doc(update),
                    ProjectSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(projectId), "프로젝트 " + operation + "완료: projectId=" + projectId);
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 " + operation + "실패: projectId=" + projectId, e);
        }
    }

    /**
     * 지정한 프로젝트를 소프트 삭제 상태로 변경합니다.
     *
     * @param projectId 소프트 삭제할 프로젝트의 ID
     */
    @Override
    public void deleteProject(Long projectId) {
        updateDeletedStatus(projectId, ProjectDeletedUpdate.deleted(), "Soft Delete");
    }

    /**
     * 지정한 프로젝트를 복원 상태로 변경합니다.
     *
     * @param projectId 복원할 프로젝트의 ID
     */
    @Override
    public void restoreProject(Long projectId) {
        updateDeletedStatus(projectId, ProjectDeletedUpdate.restored(), "삭제 복원");
    }
}
