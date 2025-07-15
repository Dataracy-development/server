package com.dataracy.modules.data.adapter.persistence.impl;

import com.dataracy.modules.data.adapter.persistence.entity.DataEntity;
import com.dataracy.modules.data.adapter.persistence.entity.DataMetadataEntity;
import com.dataracy.modules.data.adapter.persistence.mapper.DataEntityMapper;
import com.dataracy.modules.data.adapter.persistence.mapper.DataMetadataEntityMapper;
import com.dataracy.modules.data.adapter.persistence.repository.DataJpaRepository;
import com.dataracy.modules.data.adapter.persistence.repository.DataMetadataJpaRepository;
import com.dataracy.modules.data.application.port.out.DataMetadataRepositoryPort;
import com.dataracy.modules.data.application.port.out.DataRepositoryPort;
import com.dataracy.modules.data.domain.exception.DataException;
import com.dataracy.modules.data.domain.model.Data;
import com.dataracy.modules.data.domain.model.DataMetadata;
import com.dataracy.modules.data.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.Metadata;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DataMetadataRepositoryAdapter implements DataMetadataRepositoryPort {
    private final DataMetadataJpaRepository dataMetadataJpaRepository;
    private final DataJpaRepository dataJpaRepository;

    @Override
    public void saveMetadata(Long dataId, DataMetadata metadata) {
        try {
            DataMetadataEntity dataMetadataEntity = dataMetadataJpaRepository.save(DataMetadataEntityMapper.toEntity(metadata));
            DataEntity dataEntity = dataJpaRepository.findById(dataId)
                    .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
            dataEntity.updateMetadata(dataMetadataEntity);
        } catch (Exception e) {
            throw new DataException(DataErrorStatus.FAIL_UPLOAD_DATA);
        }
    }
}
