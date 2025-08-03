package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonData;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
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
     * @param projectId viewCount를 증가시킬 프로젝트의 ID
     * @param increment 증가시킬 값
     */
    public void increaseViewCount(Long projectId, Long increment) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(projectId.toString())
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source("ctx._source.viewCount += params.count")
                                            .params("count", JsonData.of(increment))
                                    )
                            ),
                    ProjectSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(projectId), "프로젝트 viewCount 증분 업데이트 완료 - projectId=" + projectId);
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "프로젝트 viewCount 증분 업데이트 실패 - projectId=" + projectId, e);
        }
    }
}
