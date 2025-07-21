package com.dataracy.modules.dataset.adapter.jpa.impl;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataMetadataEntityMapper;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataMetadataJpaRepository;
import com.dataracy.modules.dataset.application.port.out.DataMetadataRepositoryPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DataMetadataRepositoryAdapter implements DataMetadataRepositoryPort {
    private final DataMetadataJpaRepository dataMetadataJpaRepository;
    private final DataJpaRepository dataJpaRepository;

    /**
     * 지정된 데이터 ID에 해당하는 데이터 엔티티에 메타데이터를 저장합니다.
     *
     * 데이터 엔티티가 존재하지 않으면 {@code DataException}이 {@code NOT_FOUND_DATA} 상태로 발생하며,
     * 저장 과정에서 예외가 발생할 경우 {@code FAIL_UPLOAD_DATA} 상태의 {@code DataException}이 발생합니다.
     *
     * @param dataId 메타데이터를 저장할 데이터의 ID
     * @param metadata 저장할 메타데이터 도메인 객체
     */
    @Override
    public void saveMetadata(Long dataId, DataMetadata metadata) {
        try {
            DataEntity dataEntity = dataJpaRepository.findById(dataId)
                    .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
            DataMetadataEntity dataMetadataEntity = DataMetadataEntityMapper.toEntity(metadata);
            dataMetadataEntity.updateData(dataEntity);
            dataMetadataJpaRepository.save(dataMetadataEntity);
        } catch (Exception e) {
            throw new DataException(DataErrorStatus.FAIL_UPLOAD_DATA);
        }
    }
}
