package com.dataracy.modules.data.adapter.persistence.impl;

import com.dataracy.modules.data.adapter.persistence.entity.DataEntity;
import com.dataracy.modules.data.adapter.persistence.mapper.DataEntityMapper;
import com.dataracy.modules.data.adapter.persistence.repository.jpa.DataJpaRepository;
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

    /**
     * 도메인 Data 객체를 데이터베이스에 저장하고 저장된 결과를 반환합니다.
     *
     * @param data 저장할 도메인 Data 객체
     * @return 저장된 Data 객체
     * @throws DataException 데이터 저장 중 오류가 발생하면 업로드 실패 상태와 함께 예외를 발생시킵니다.
     */
    @Override
    public Data saveData(Data data) {
        try {
            DataEntity dataEntity = dataJpaRepository.save(DataEntityMapper.toEntity(data));
            return DataEntityMapper.toDomain(dataEntity);
        } catch (Exception e) {
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
     * 지정된 데이터 ID에 해당하는 데이터 엔티티의 데이터 파일 URL을 업데이트합니다.
     *
     * 데이터가 존재하지 않을 경우 DataException을 발생시킵니다.
     *
     * @param dataId        데이터 엔티티의 식별자
     * @param dataFileUrl   새로 설정할 데이터 파일 URL
     */
    @Override
    public void updateDataFile(Long dataId, String dataFileUrl) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        dataEntity.updateDataFile(dataFileUrl);
    }

    /**
     * 데이터 ID에 해당하는 데이터의 썸네일 파일 URL을 새 값으로 변경합니다.
     *
     * @param dataId 썸네일 파일 URL을 변경할 데이터의 ID
     * @param thumbnailFileUrl 변경할 새 썸네일 파일 URL
     * @throws DataException 데이터가 존재하지 않을 경우 발생합니다.
     */
    @Override
    public void updateThumbnailFile(Long dataId, String thumbnailFileUrl) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        dataEntity.updateThumbnailFile(thumbnailFileUrl);
    }

    /**
     * 지정된 ID를 가진 데이터 엔티티가 저장소에 존재하는지 여부를 반환합니다.
     *
     * @param dataId 존재 여부를 확인할 데이터의 ID
     * @return 데이터가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsDataById(Long dataId) {
        return dataJpaRepository.existsById(dataId);
    }
}
