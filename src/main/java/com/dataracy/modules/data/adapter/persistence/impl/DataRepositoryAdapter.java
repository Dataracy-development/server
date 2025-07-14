package com.dataracy.modules.data.adapter.persistence.impl;

import com.dataracy.modules.data.adapter.persistence.entity.DataEntity;
import com.dataracy.modules.data.adapter.persistence.mapper.DataEntityMapper;
import com.dataracy.modules.data.adapter.persistence.repository.DataJpaRepository;
import com.dataracy.modules.data.application.port.out.DataRepositoryPort;
import com.dataracy.modules.data.domain.exception.DataException;
import com.dataracy.modules.data.domain.model.Data;
import com.dataracy.modules.data.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DataRepositoryAdapter implements DataRepositoryPort {
    private final DataJpaRepository dataJpaRepository;

    @Override
    public Data saveData(Data data) {
        try {
            DataEntity dataEntity = dataJpaRepository.save(DataEntityMapper.toEntity(data));
            return DataEntityMapper.toDomain(dataEntity);
        } catch (Exception e) {
            throw new DataException(DataErrorStatus.FAIL_UPLOAD_DATA);
        }
    }

    @Override
    public Optional<Data> findDataById(Long dataId) {
        return dataJpaRepository.findById(dataId)
                .map(DataEntityMapper::toDomain);
    }

    @Override
    public void updateDataFile(Long dataId, String dataFileUrl) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        dataEntity.updateDataFile(dataFileUrl);
    }

    @Override
    public void updateThumbnailFile(Long dataId, String thumbnailFileUrl) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        dataEntity.updateThumbnailFile(thumbnailFileUrl);
    }
}
