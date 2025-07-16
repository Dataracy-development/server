package com.dataracy.modules.data.application.port.out;

import com.dataracy.modules.data.domain.model.DataMetadata;

public interface DataMetadataRepositoryPort {
    void saveMetadata(Long dataId, DataMetadata metadata);
}
