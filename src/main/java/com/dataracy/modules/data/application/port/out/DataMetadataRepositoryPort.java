package com.dataracy.modules.data.application.port.out;

import com.dataracy.modules.data.domain.model.DataMetadata;

public interface DataMetadataRepositoryPort {
    /**
 * 지정된 데이터 ID에 해당하는 메타데이터를 저장합니다.
 *
 * @param dataId 메타데이터를 저장할 데이터의 고유 식별자
 * @param metadata 저장할 데이터 메타데이터 객체
 */
void saveMetadata(Long dataId, DataMetadata metadata);
}
