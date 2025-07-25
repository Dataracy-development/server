package com.dataracy.modules.dataset.application.port.elasticsearch;

import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataSearchDocument;

public interface DataIndexingPort {
/**
 * 주어진 데이터 검색 문서를 Elasticsearch에 인덱싱합니다.
 *
 * @param doc 인덱싱할 데이터 검색 문서 객체
 */
void index(DataSearchDocument doc);
    /**
 * 주어진 데이터 ID에 해당하는 문서를 삭제된 상태로 표시합니다.
 *
 * @param dataId 삭제 상태로 표시할 데이터의 ID
 */
void markAsDeleted(Long dataId);
    /**
 * 지정된 데이터 ID에 해당하는 문서를 복구 상태로 표시합니다.
 *
 * @param dataId 복구할 데이터의 고유 식별자
 */
void markAsRestore(Long dataId);
}
