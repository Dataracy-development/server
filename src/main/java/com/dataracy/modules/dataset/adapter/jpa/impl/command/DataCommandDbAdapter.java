package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.port.out.command.create.CreateDataPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataFilePort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateThumbnailFilePort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DataCommandDbAdapter implements
        CreateDataPort,
        UpdateDataFilePort,
        UpdateThumbnailFilePort,
        UpdateDataPort
{
    private final DataJpaRepository dataJpaRepository;

    @Override
    public Data saveData(Data data) {
        DataEntity dataEntity = DataEntityMapper.toEntity(data);
        Data savedData = DataEntityMapper.toDomain(dataJpaRepository.save(dataEntity));
        LoggerFactory.db().logSave("DataEntity", String.valueOf(savedData.getId()), "데이터셋 파일 업로드가 완료되었습니다.");
        return savedData;
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
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("DataEntity", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        dataEntity.updateDataFile(dataFileUrl);
        dataJpaRepository.save(dataEntity);
        LoggerFactory.db().logUpdate("DataEntity", String.valueOf(dataId), "데이터셋 파일 업데이트가 완료되었습니다.");
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
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("DataEntity", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        dataEntity.updateThumbnailFile(thumbnailFileUrl);
        dataJpaRepository.save(dataEntity);
        LoggerFactory.db().logUpdate("DataEntity", String.valueOf(dataId), "데이터셋 썸네일 이미지 파일 업데이트가 완료되었습니다.");
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
    public void modifyData(Long dataId, ModifyDataRequest requestDto) {
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("DataEntity", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        dataEntity.modify(requestDto);
        dataJpaRepository.save(dataEntity);
        LoggerFactory.db().logUpdate("DataEntity", String.valueOf(dataId), "데이터셋 업데이트가 완료되었습니다.");
    }
}
