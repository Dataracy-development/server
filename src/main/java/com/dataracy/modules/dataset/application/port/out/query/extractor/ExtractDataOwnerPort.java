package com.dataracy.modules.dataset.application.port.out.query.extractor;

import java.util.Optional;

public interface ExtractDataOwnerPort {
    /**
     * 지정된 데이터 ID에 연결된 사용자 ID를 반환합니다.
     *
     * @param dataId 사용자 ID를 조회할 데이터의 고유 식별자
     * @return 해당 데이터에 연결된 사용자 ID
     */
    Long findUserIdByDataId(Long dataId);
    /**
     * 삭제된 데이터를 포함하여 지정된 데이터 ID에 연결된 사용자 ID를 반환합니다.
     *
     * @param dataId 사용자 ID를 조회할 데이터의 고유 식별자
     * @return 해당 데이터에 연결된 사용자 ID
     */
    Long findUserIdIncludingDeleted(Long dataId);

    Optional<String> findDataFileUrlById(Long dataId);
}
