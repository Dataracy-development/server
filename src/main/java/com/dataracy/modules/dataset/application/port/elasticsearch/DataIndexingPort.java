package com.dataracy.modules.dataset.application.port.elasticsearch;

import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;

public interface DataIndexingPort {
/**
 * 데이터 검색 문서를 인덱싱합니다.
 *
 * @param doc 인덱싱할 DataSearchDocument 객체
 */
void index(DataSearchDocument doc);
}
