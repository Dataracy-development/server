package com.dataracy.modules.project.application.port.elasticsearch;

import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;

public interface ProjectIndexingPort {
    /**
 * 주어진 프로젝트 검색 문서를 색인합니다.
 *
 * @param doc 색인할 프로젝트 검색 문서
 */
void index(ProjectSearchDocument doc);
    void markAsDeleted(Long projectId);
    void restoreDeleted(Long projectId);
}
