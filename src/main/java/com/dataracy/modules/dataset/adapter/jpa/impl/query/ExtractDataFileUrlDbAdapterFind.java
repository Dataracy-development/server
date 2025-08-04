package com.dataracy.modules.dataset.adapter.jpa.impl.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.port.out.query.extractor.FindDownloadDataFileUrlPort;
import com.dataracy.modules.dataset.application.port.out.query.extractor.ExtractDataOwnerPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExtractDataFileUrlDbAdapterFind implements
        ExtractDataOwnerPort,
        FindDownloadDataFileUrlPort
{
    private final DataJpaRepository dataJpaRepository;

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
        Instant startTime = LoggerFactory.db().logQueryStart("DataEntity", "[findById] 데이터셋의 작성자 아이디 조회 시작 dataId=" + dataId);
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("DataEntity", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        LoggerFactory.db().logQueryEnd("DataEntity", "[findById] 데이터셋의 작성자 아이디 조회 종료 dataId=" + dataId, startTime);
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
        Instant startTime = LoggerFactory.db().logQueryStart("DataEntity", "[findIncludingDeletedData] 삭제된 데이터셋을 포함하여 데이터셋의 작성자 아이디 조회 시작 dataId=" + dataId);
        DataEntity dataEntity = dataJpaRepository.findIncludingDeletedData(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning("DataEntity", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        LoggerFactory.db().logQueryEnd("DataEntity", "[findIncludingDeletedData] 삭제된 데이터셋을 포함하여 데이터셋의 작성자 아이디 조회 종료 dataId=" + dataId, startTime);
        return dataEntity.getUserId();
    }

    /**
     * 주어진 데이터 ID에 해당하는 데이터셋 파일을 조회하여 반환합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 데이터셋 파일이 존재하면 해당 파일의 정보를 포함한 Optional, 없으면 Optional.empty()
     */
    @Override
    public Optional<String> findDataFileUrlById(Long dataId) {
        Instant startTime = LoggerFactory.db().logQueryStart("DataEntity", "[findDataFileUrlById] 데이터셋의 파일 url 조회 시작 dataId=" + dataId);
        Optional<String> dataFileUrl = dataJpaRepository.findDataFileUrlById(dataId);
        LoggerFactory.db().logQueryEnd("DataEntity", "[findDataFileUrlById] 데이터셋의 파일 url 조회 종료 dataId=" + dataId, startTime);
        return dataFileUrl;
    }

    /**
     * 주어진 데이터 ID에 해당하는 데이터셋 파일을 조회하여 반환합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 데이터셋 파일이 존재하면 해당 파일의 정보를 포함한 Optional, 없으면 Optional.empty()
     */
    @Override
    public Optional<String> findDownloadedDataFileUrl(Long dataId) {
        Instant startTime = LoggerFactory.db().logQueryStart("DataEntity", "[findDataFileUrlById] 주어진 데이터 ID에 해당하는 데이터셋 파일 url 조회 시작 dataId=" + dataId);
        Optional<String> dataFileUrl = dataJpaRepository.findDataFileUrlById(dataId);
        LoggerFactory.db().logQueryEnd("DataEntity", "[findDataFileUrlById] 주어진 데이터 ID에 해당하는 데이터셋 파일 조회 종료 dataId=" + dataId, startTime);
        return dataFileUrl;
    }
}
