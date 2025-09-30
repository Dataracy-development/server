package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectCommentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("updateProjectCommentEsAdapter")
@RequiredArgsConstructor
public class UpdateProjectCommentEsAdapter implements UpdateProjectCommentPort {
    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";
    
    private static final String INCREASE_COMMENT_COUNT_SCRIPT = """
            if (ctx._source.commentCount == null) {
                ctx._source.commentCount = 1;
            } else {
                ctx._source.commentCount += 1;
            }
            """;
    
    private static final String DECREASE_COMMENT_COUNT_SCRIPT = """
            if (ctx._source.commentCount != null && ctx._source.commentCount > 0) {
                ctx._source.commentCount -= 1;
            } else {
                ctx._source.commentCount = 0;
            }
            """;

    /**
     * 지정된 프로젝트의 Elasticsearch 문서에서 commentCount 필드를 1 증가시킵니다.
     *
     * commentCount가 null이면 1로 초기화하고, 값이 있으면 1을 더합니다.
     *
     * @param projectId commentCount를 증가시킬 프로젝트의 ID
     */
    @Override
    public void increaseCommentCount(Long projectId) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(String.valueOf(projectId))
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source(INCREASE_COMMENT_COUNT_SCRIPT)
                                    )
                            )
                            .upsert(ProjectSearchDocument.builder()
                                    .id(projectId)
                                    .commentCount(1L)
                                    .isDeleted(false)
                                    .build()),
                    ProjectSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(projectId), "프로젝트 commentCount 증분 업데이트 완료 - projectId=" + projectId);
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 commentCount 증분 업데이트 실패 - projectId=" + projectId, e);
            throw new EsUpdateException("ES update failed: projectId=" + projectId, e);
        }
    }

    /**
     * 지정된 프로젝트의 Elasticsearch 문서에서 commentCount 필드를 1 감소시킵니다.
     *
     * commentCount가 null이거나 0 이하인 경우 0으로 설정됩니다.
     *
     * @param projectId commentCount를 감소시킬 프로젝트의 ID
     */
    @Override
    public void decreaseCommentCount(Long projectId) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(String.valueOf(projectId))
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source(DECREASE_COMMENT_COUNT_SCRIPT)
                                    )
                            )
                            .upsert(ProjectSearchDocument.builder()
                                    .id(projectId)
                                    .commentCount(0L)
                                    .isDeleted(false)
                                    .build()),
                    ProjectSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(projectId), "프로젝트 commentCount 감분 업데이트 완료 - projectId=" + projectId);
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 commentCount 감분 업데이트 실패 - projectId=" + projectId, e);
            throw new EsUpdateException("ES update failed: projectId=" + projectId, e);
        }
    }
}
