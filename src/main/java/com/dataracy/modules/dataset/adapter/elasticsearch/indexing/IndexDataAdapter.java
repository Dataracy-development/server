package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.port.out.indexing.IndexDataPort;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IndexDataAdapter implements IndexDataPort {
  private final ElasticsearchClient client;
  private static final String INDEX = "data_index";

  /**
   * 주어진 DataSearchDocument를 Elasticsearch의 "data_index" 인덱스에 저장합니다. 인덱싱 중 IOException이 발생하면 예외를 외부로
   * 전달하지 않고 로깅만 수행합니다.
   *
   * @param doc Elasticsearch에 저장할 데이터셋 문서
   */
  @Override
  public void index(DataSearchDocument doc) {
    String docId = doc.id().toString();
    try {
      client.index(i -> i.index(INDEX).id(docId).document(doc));
      LoggerFactory.elastic().logIndex(INDEX, docId, "데이터셋 인덱싱 완료");
    } catch (IOException e) {
      LoggerFactory.elastic().logError(INDEX, "데이터셋 인덱싱 실패: docId=" + docId, e);
    }
  }
}
