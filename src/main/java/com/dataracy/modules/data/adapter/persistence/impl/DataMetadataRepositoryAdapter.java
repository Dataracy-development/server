package com.dataracy.modules.data.adapter.persistence.impl;

import com.dataracy.modules.data.adapter.persistence.entity.DataMetadataEntity;
import com.dataracy.modules.data.adapter.persistence.mapper.DataEntityMapper;
import com.dataracy.modules.data.adapter.persistence.mapper.DataMetadataEntityMapper;
import com.dataracy.modules.data.adapter.persistence.repository.DataJpaRepository;
import com.dataracy.modules.data.adapter.persistence.repository.DataMetadataJpaRepository;
import com.dataracy.modules.data.application.port.out.DataMetadataRepositoryPort;
import com.dataracy.modules.data.domain.exception.DataException;
import com.dataracy.modules.data.domain.model.Data;
import com.dataracy.modules.data.domain.model.DataMetadata;
import com.dataracy.modules.data.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DataMetadataRepositoryAdapter implements DataMetadataRepositoryPort {
    private final DataMetadataJpaRepository dataMetadataJpaRepository;

    @Override
    public void saveMetadata(Data data, DataMetadata metadata) {
        try {
            DataMetadataEntity dataMetadataEntity = DataMetadataEntityMapper.toEntity(metadata);
            dataMetadataEntity.updateData(DataEntityMapper.toEntity(data));
            dataMetadataJpaRepository.save(dataMetadataEntity);
        } catch (Exception e) {
            throw new DataException(DataErrorStatus.FAIL_UPLOAD_DATA);
        }
    }
}
