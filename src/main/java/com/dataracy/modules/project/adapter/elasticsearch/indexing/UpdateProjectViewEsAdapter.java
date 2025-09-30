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
import java.util.Map;

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
    @Override
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

    /**
     * 여러 프로젝트의 조회수를 배치로 증가시킵니다.
     *
     * @param viewCountUpdates 프로젝트 ID와 증가시킬 조회수 값의 맵
     */
    @Override
    public void increaseViewCountBatch(Map<Long, Long> viewCountUpdates) {
        if (viewCountUpdates.isEmpty()) {
            return;
        }

        // ES에서는 개별 처리 (배치 업데이트는 복잡하므로 개별 처리로 구현)
        for (Map.Entry<Long, Long> entry : viewCountUpdates.entrySet()) {
            try {
                increaseViewCount(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LoggerFactory.elastic().logError(INDEX, 
                    "배치 처리 중 개별 프로젝트 업데이트 실패 - projectId=" + entry.getKey(), e);
                // 개별 실패가 전체에 영향을 주지 않도록 계속 진행
            }
        }
        
        LoggerFactory.elastic().logUpdate(INDEX, "배치 처리", 
            "프로젝트 viewCount 배치 업데이트 완료. 처리된 프로젝트 수: " + viewCountUpdates.size());
    }
}
