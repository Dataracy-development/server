package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.exception.EsUpdateException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectLikePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("updateProjectLikeEsAdapter")
@RequiredArgsConstructor
public class UpdateProjectLikeEsAdapter implements UpdateProjectLikePort {
    private final ElasticsearchClient client;
    private static final String INDEX = "project_index";

    /**
     * 지정된 프로젝트의 Elasticsearch 문서에서 likeCount 필드를 1 증가시킵니다.
     *
     * @param projectId likeCount를 증가시킬 프로젝트의 ID
     */
    @Override
    public void increaseLikeCount(Long projectId) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(String.valueOf(projectId))
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source("""
                                                    if (ctx._source.likeCount == null) {
                                                        ctx._source.likeCount = 1;
                                                    } else {
                                                        ctx._source.likeCount += 1;
                                                    }
                                                    """)
                                    )
                            )
                            .upsert(ProjectSearchDocument.builder()
                                    .id(projectId)
                                    .likeCount(1L)
                                    .isDeleted(false)
                                    .build()),
                    ProjectSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(projectId), "프로젝트 likeCount 증분 업데이트 완료 - projectId=" + projectId);
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 likeCount 증분 업데이트 실패 - projectId=" + projectId, e);
            throw new EsUpdateException("ES update failed: projectId=" + projectId, e);
        }
    }

    /**
     * 지정된 프로젝트의 Elasticsearch 문서에서 likeCount 필드를 1 감소시킵니다.
     *
     * likeCount가 0보다 크면 1 감소시키고, 그렇지 않으면 0으로 설정합니다.
     *
     * @param projectId likeCount를 감소시킬 프로젝트의 ID
     */
    @Override
    public void decreaseLikeCount(Long projectId) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(String.valueOf(projectId))
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source("""
                                            if (ctx._source.likeCount != null && ctx._source.likeCount > 0) {
                                                ctx._source.likeCount -= 1;
                                            } else {
                                                ctx._source.likeCount = 0;
                                            }
                                            """)
                                    )
                            )
                            .upsert(ProjectSearchDocument.builder()
                                    .id(projectId)
                                    .likeCount(0L)
                                    .isDeleted(false)
                                    .build()),
                    ProjectSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(projectId), "프로젝트 likeCount 감분 업데이트 완료 - projectId=" + projectId);
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 likeCount 감분 업데이트 실패 - projectId=" + projectId, e);
            throw new EsUpdateException("ES update failed: projectId=" + projectId, e);
        }
    }
}
