package com.dataracy.modules.dataset.application.port.out.indexing;

import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;

public interface IndexDataPort {
    /**
 * 데이터 검색 문서를 Elasticsearch에 인덱싱합니다.
 *
 * @param doc 인덱싱할 데이터 검색 문서
 */
    void index(DataSearchDocument doc);
}
