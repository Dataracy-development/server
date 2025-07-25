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

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DataMetadataRepositoryAdapter implements DataMetadataRepositoryPort {
    private final DataMetadataJpaRepository dataMetadataJpaRepository;
    private final DataJpaRepository dataJpaRepository;

    /****
     * 지정된 데이터 ID에 해당하는 데이터 엔티티에 메타데이터를 생성하거나 갱신하여 저장합니다.
     *
     * 데이터 엔티티가 존재하지 않으면 {@code NOT_FOUND_DATA} 상태의 {@code DataException}이 발생합니다.
     * 저장 과정에서 예기치 않은 오류가 발생하면 {@code FAIL_UPLOAD_DATA} 상태의 {@code DataException}이 발생합니다.
     *
     * @param dataId 메타데이터를 저장할 데이터의 ID
     * @param metadata 저장 또는 갱신할 메타데이터 도메인 객체
     */
    @Override
    public void saveMetadata(Long dataId, DataMetadata metadata) {
        try {
            DataEntity dataEntity = dataJpaRepository.findById(dataId)
                    .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));

            Optional<DataMetadataEntity> existing = dataMetadataJpaRepository.findByDataId(dataId);

            if (existing.isPresent()) {
                DataMetadataEntity entity = existing.get();
                entity.updateFrom(metadata);
                dataMetadataJpaRepository.save(entity);
            } else {
                DataMetadataEntity newEntity = DataMetadataEntityMapper.toEntity(metadata);
                newEntity.updateData(dataEntity);
                dataMetadataJpaRepository.save(newEntity);
            }
        } catch (DataException de) {
            // NOT_FOUND_DATA 등 기존 상태 유지
            throw de;
        } catch (Exception e) {
            // 원인 보존
            throw new DataException(DataErrorStatus.FAIL_UPLOAD_DATA);
        }
    }
}
