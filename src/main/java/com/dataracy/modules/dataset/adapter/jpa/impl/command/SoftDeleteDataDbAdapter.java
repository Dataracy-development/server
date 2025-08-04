package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository("softDeleteDataDbAdapter")
@RequiredArgsConstructor
public class SoftDeleteDataDbAdapter implements SoftDeleteDataPort {
    private final DataJpaRepository dataJpaRepository;
    /**
     * 지정된 데이터 ID에 해당하는 데이터를 논리적으로 삭제합니다.
     *
     * 데이터가 존재하지 않을 경우 {@code DataException}이 발생합니다.
     *
     * @param dataId 삭제할 데이터의 ID
     * @throws DataException 데이터가 존재하지 않을 경우 발생
     */
    @Override
    public void deleteData(Long dataId) {
        DataEntity data = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("DataEntity", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        data.delete();
        dataJpaRepository.save(data);
        LoggerFactory.db().logUpdate("DataEntity", String.valueOf(dataId), "데이터셋 soft delete 삭제가 완료되었습니다.");
    }

    /**
     * 지정한 ID의 논리적으로 삭제된 데이터를 복구합니다.
     *
     * @param dataId 복구할 데이터의 ID
     * @throws DataException 해당 ID의 데이터를 찾을 수 없는 경우 발생하며, 상태는 NOT_FOUND_DATA입니다.
     */
    @Override
    public void restoreData(Long dataId) {
        DataEntity data = dataJpaRepository.findIncludingDeletedData(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("DataEntity", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        data.restore();
        dataJpaRepository.save(data);
        LoggerFactory.db().logUpdate("DataEntity", String.valueOf(dataId), "데이터셋 삭제 복원이 완료되었습니다.");
    }
}
