package com.dataracy.modules.dataset.adapter.jpa.impl;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.dto.request.DataModifyRequest;
import com.dataracy.modules.dataset.application.port.out.DataRepositoryPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DataRepositoryAdapter implements DataRepositoryPort {
    private final DataJpaRepository dataJpaRepository;

    /**
     * 도메인 Data 객체를 데이터베이스에 저장한 후, 저장된 결과를 반환합니다.
     *
     * @param data 저장할 도메인 Data 객체
     * @return 저장된 Data 객체
     * @throws DataException 데이터 저장 중 오류가 발생하면 업로드 실패 상태로 예외가 발생합니다.
     */
    @Override
    public Data saveData(Data data) {
        try {
            DataEntity dataEntity = dataJpaRepository.save(DataEntityMapper.toEntity(data));
            return DataEntityMapper.toDomain(dataEntity);
        } catch (Exception e) {
            log.error("데이터 저장 실패", e);
            throw new DataException(DataErrorStatus.FAIL_UPLOAD_DATA);
        }
    }

    /**
     * 주어진 ID에 해당하는 데이터를 조회하여 Optional로 반환합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 데이터가 존재하면 Optional<Data>로 반환하며, 없으면 Optional.empty()를 반환합니다.
     */
    @Override
    public Optional<Data> findDataById(Long dataId) {
        return dataJpaRepository.findById(dataId)
                .map(DataEntityMapper::toDomain);
    }

    /**
     * 데이터 ID에 해당하는 데이터 엔티티의 데이터 파일 URL을 새 값으로 변경합니다.
     *
     * 데이터가 존재하지 않으면 DataException이 발생합니다.
     *
     * @param dataId 데이터 엔티티의 ID
     * @param dataFileUrl 변경할 데이터 파일의 URL
     */
    @Override
    public void updateDataFile(Long dataId, String dataFileUrl) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        dataEntity.updateDataFile(dataFileUrl);
        dataJpaRepository.save(dataEntity);
    }

    /**
     * 지정한 데이터 ID의 썸네일 파일 URL을 새로운 값으로 업데이트합니다.
     *
     * @param dataId 업데이트할 데이터의 ID
     * @param thumbnailFileUrl 새로 설정할 썸네일 파일 URL
     * @throws DataException 데이터가 존재하지 않을 경우 발생합니다.
     */
    @Override
    public void updateThumbnailFile(Long dataId, String thumbnailFileUrl) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        dataEntity.updateThumbnailFile(thumbnailFileUrl);
        dataJpaRepository.save(dataEntity);
    }

    /**
     * 주어진 ID의 데이터가 저장소에 존재하는지 확인합니다.
     *
     * @param dataId 존재 여부를 확인할 데이터의 ID
     * @return 데이터가 존재하면 true, 존재하지 않으면 false
     */
    @Override
    public boolean existsDataById(Long dataId) {
        return dataJpaRepository.existsById(dataId);
    }

    /**
     * 주어진 데이터 ID에 해당하는 데이터의 사용자 ID를 반환합니다.
     *
     * 데이터가 존재하지 않을 경우 {@code DataException}이 발생합니다.
     *
     * @param dataId 사용자 ID를 조회할 데이터의 ID
     * @return 데이터에 연결된 사용자 ID
     */
    @Override
    public Long findUserIdByDataId(Long dataId) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        return dataEntity.getUserId();
    }

    /**
     * 주어진 데이터 ID에 해당하는 데이터 엔티티(삭제된 데이터 포함)의 사용자 ID를 반환합니다.
     *
     * @param dataId 사용자 ID를 조회할 데이터의 ID
     * @return 데이터에 연결된 사용자 ID
     * @throws DataException 데이터가 존재하지 않을 경우 {@code DataErrorStatus.NOT_FOUND_DATA} 상태로 예외가 발생합니다.
     */
    @Override
    public Long findUserIdIncludingDeleted(Long dataId) {
        DataEntity dataEntity = dataJpaRepository.findIncludingDeletedData(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        return dataEntity.getUserId();
    }

    /**
     * 지정된 데이터 ID에 해당하는 DataEntity를 찾아 DataModifyRequest로 수정한 후 저장합니다.
     *
     * 데이터가 존재하지 않을 경우 DataException이 발생합니다.
     *
     * @param dataId 수정할 데이터의 ID
     * @param requestDto 데이터 수정 요청 정보
     */
    @Override
    public void modify(Long dataId, DataModifyRequest requestDto) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));

        dataEntity.modify(requestDto);
        dataJpaRepository.save(dataEntity);
    }

    /**
     * 지정된 데이터 ID에 해당하는 데이터를 논리적으로 삭제합니다.
     *
     * 데이터가 존재하지 않을 경우 {@code DataException}이 발생합니다.
     *
     * @param dataId 삭제할 데이터의 ID
     * @throws DataException 데이터가 존재하지 않을 경우 발생
     */
    @Override
    public void delete(Long dataId) {
        DataEntity data = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        data.delete();
        dataJpaRepository.save(data);
    }

    /**
     * 지정한 ID의 논리적으로 삭제된 데이터를 복구합니다.
     *
     * @param dataId 복구할 데이터의 ID
     * @throws DataException 해당 ID의 데이터를 찾을 수 없는 경우 발생하며, 상태는 NOT_FOUND_DATA입니다.
     */
    @Override
    public void restore(Long dataId) {
        DataEntity data = dataJpaRepository.findIncludingDeletedData(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        data.restore();
        dataJpaRepository.save(data);
    }

    /**
     * 주어진 데이터 ID에 해당하는 데이터셋 파일을 조회하여 반환합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 데이터셋 파일이 존재하면 해당 파일의 정보를 포함한 Optional, 없으면 Optional.empty()
     */
    @Override
    public Optional<String> downloadDatasetFile(Long dataId) {
        return dataJpaRepository.downloadDatasetFile(dataId);
    }
}
