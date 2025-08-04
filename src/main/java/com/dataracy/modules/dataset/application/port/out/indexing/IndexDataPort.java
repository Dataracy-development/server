package com.dataracy.modules.dataset.application.port.out.indexing;

import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;

public interface IndexDataPort {
    /**
     * 주어진 데이터 검색 문서를 Elasticsearch에 인덱싱합니다.
     *
     * @param doc 인덱싱할 데이터 검색 문서 객체
     */
    void index(DataSearchDocument doc);
}
