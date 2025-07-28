package com.dataracy.modules.project.application.port.elasticsearch;

import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;

public interface ProjectIndexingPort {
    /****
 * 프로젝트 검색 문서를 색인 시스템에 추가합니다.
 *
 * @param doc 색인할 프로젝트의 검색 문서 객체
 */
void index(ProjectSearchDocument doc);
}
