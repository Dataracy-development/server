package com.dataracy.modules.data.application.port.out;

import com.dataracy.modules.data.domain.model.Data;

import java.util.Optional;

/**
 * 데이터셋 db 포트
 */
public interface DataRepositoryPort {

    Data saveData(Data data);

    Optional<Data> findDataById(Long dataId);

    void updateDataFile(Long dataId, String dataFileUrl);

    void updateThumbnailFile(Long dataId, String thumbFileUrl);
}
