/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.port.out.indexing.IndexProjectPort;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IndexProjectAdapter implements IndexProjectPort {
  private final ElasticsearchClient client;
  private static final String INDEX = "project_index";

  /**
   * 프로젝트 검색 문서를 Elasticsearch의 "project_index" 인덱스에 저장하거나 갱신합니다.
   *
   * @param doc 저장 또는 갱신할 프로젝트 검색 문서
   */
  @Override
  public void index(ProjectSearchDocument doc) {
    String docId = doc.id().toString();
    try {
      client.index(i -> i.index(INDEX).id(docId).document(doc));
      LoggerFactory.elastic().logIndex(INDEX, docId, "프로젝트 인덱싱 완료");
    } catch (IOException e) {
      LoggerFactory.elastic().logError(INDEX, "프로젝트 인덱싱 실패: docId=" + docId, e);
    }
  }
}
