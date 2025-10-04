package com.dataracy.modules.project.application.port.out.indexing;

import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;

public interface IndexProjectPort {
  /**
   * 프로젝트 검색 문서를 색인 시스템에 등록합니다.
   *
   * @param doc 색인 대상이 되는 프로젝트 검색 문서
   */
  void index(ProjectSearchDocument doc);
}
