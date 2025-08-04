package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.domain.model.Data;

import java.util.Optional;

public interface FindDataWithMetadataPort {
    /**
     * 지정된 ID에 해당하는 데이터와 그 메타데이터를 조회합니다.
     *
     * @param dataId 조회할 데이터의 고유 식별자
     * @return 데이터와 메타데이터가 존재하면 해당 Data 객체를 포함한 Optional, 없으면 빈 Optional
     */
    Optional<Data> findDataWithMetadataById(Long dataId);
}
