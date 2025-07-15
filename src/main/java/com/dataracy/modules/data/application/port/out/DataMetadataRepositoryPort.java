package com.dataracy.modules.data.application.port.out;

import com.dataracy.modules.data.domain.model.Data;
import com.dataracy.modules.data.domain.model.DataMetadata;

public interface DataMetadataRepositoryPort {
    void saveMetadata(Data data, DataMetadata metadata);
}
