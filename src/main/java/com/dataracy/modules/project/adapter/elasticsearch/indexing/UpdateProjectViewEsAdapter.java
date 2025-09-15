package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonData;
import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectViewPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("updateProjectViewEsAdapter")
@RequiredArgsConstructor
public class UpdateProjectViewEsAdapter implements UpdateProjectViewPort {
    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";

    /**
     * 지정된 프로젝트의 Elasticsearch 문서에서 viewCount 필드를 주어진 값만큼 증가시킵니다.
     *
     * 프로젝트 문서가 존재하지 않을 경우, viewCount를 increment 값으로 하여 새로 생성합니다.
     *
     * @param projectId viewCount를 증가시킬 프로젝트의 ID
     * @param increment viewCount에 더할 값
     */
    public void increaseViewCount(Long projectId, Long increment) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(String.valueOf(projectId))
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source("""
                                                if (ctx._source.viewCount == null) {
                                                    ctx._source.viewCount = params.count;
                                                } else {
                                                    ctx._source.viewCount += params.count;
                                                }
                                            """)
                                            .params("count", JsonData.of(increment))
                                    )
                            )
                            .upsert(ProjectSearchDocument.builder()
                                    .id(projectId)
                                    .viewCount(increment)
                                    .isDeleted(false)
                                    .build()),
                    ProjectSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(projectId), "프로젝트 viewCount 증분 업데이트 완료 - projectId=" + projectId);
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 viewCount 증분 업데이트 실패 - projectId=" + projectId, e);
            throw new EsUpdateException("ES update failed: projectId=" + projectId, e);
        }
    }
}
