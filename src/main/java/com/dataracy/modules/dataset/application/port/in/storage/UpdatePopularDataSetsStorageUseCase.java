package com.dataracy.modules.dataset.application.port.in.storage;

public interface UpdatePopularDataSetsStorageUseCase {
    /**
     * 저장소에 데이터가 없을 때 즉시 업데이트합니다.
     * 
     * @param size 조회할 데이터셋 개수
     */
    void warmUpCacheIfNeeded(int size);
}
