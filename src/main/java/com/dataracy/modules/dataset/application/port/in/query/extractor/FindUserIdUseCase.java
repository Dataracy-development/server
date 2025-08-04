package com.dataracy.modules.dataset.application.port.in.query.extractor;

public interface FindUserIdUseCase {
    /**
     * 주어진 데이터 ID에 해당하는 사용자 ID를 반환합니다.
     *
     * @param dataId 사용자 ID를 조회할 데이터의 식별자
     * @return 데이터 ID에 연결된 사용자 ID
     */
    Long findUserIdByDataId(Long dataId);
}
